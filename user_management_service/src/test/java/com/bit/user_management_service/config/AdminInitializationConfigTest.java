package com.bit.user_management_service.config;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminInitializationConfigTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoderConfig passwordEncoderConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializationConfig adminInitializationConfig;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_Initialize_Admin_If_There_Is_Not_Any(){
        Role adminRole = new Role("ROLE_ADMIN");
        adminRole.setId(1L);

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRoles(any())).thenReturn(Collections.emptyList());
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void test_Initialize_Admin_If_There_Is_Any(){
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");

        User existingAdminUser = User.builder()
                        .id(1L)
                        .firstName("emirhan")
                        .lastName("cebiroglu")
                        .userCode("emirhancebiroglu21@hotmail.com")
                        .password("Emirhan2165")
                        .roles(Collections.singleton(adminRole))
                        .build();

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);

        when(userRepository.findByRoles(adminRoles)).thenReturn(Collections.singletonList(existingAdminUser));

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test()
    public void test_Initialize_Admin_If_There_Is_No_Admin_Role(){
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminInitializationConfig.initializeAdmin());
        verify(userRepository, times(0)).save(any(User.class));
    }
}