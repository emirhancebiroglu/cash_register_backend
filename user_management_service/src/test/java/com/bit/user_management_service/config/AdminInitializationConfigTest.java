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
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitializeAdminIfNoAdminExists() {
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
    public void testInitializeAdminIfAdminExists() {
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName("ROLE_ADMIN");

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);

        User existingAdminUser = User.builder()
                .id(1L)
                .firstName("emirhan")
                .lastName("cebiroglu")
                .email("emirhan@hotmail.com")
                .userCode("admin")
                .password("Emirhan2165")
                .roles(Collections.singleton(adminRole))
                .build();

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRoles(adminRoles)).thenReturn(Collections.singletonList(existingAdminUser));

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testInitializeAdminIfNoAdminRole() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminInitializationConfig.initializeAdmin());
        verify(userRepository, never()).save(any(User.class));
    }
}
