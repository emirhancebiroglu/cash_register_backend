package com.bit.user_management_service.config;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    private Role adminRole;
    private Set<Role> roles;
    private User adminUser;

    @BeforeEach
    public void setup() {
        adminRole = new Role("ROLE_ADMIN");

        roles = new HashSet<>();
        roles.add(adminRole);

        adminUser = User.builder()
                .firstName("admin")
                .lastName("admin")
                .email("admin@gmail.com")
                .userCode("admin")
                .password("password")
                .roles(roles)
                .build();
    }

    @Test
    public void initializeAdminIfNoAdminExists() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRoles(any())).thenReturn(Collections.emptyList());
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void initializeAdminIfAdminExists() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRoles(roles)).thenReturn(Collections.singletonList(adminUser));

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
