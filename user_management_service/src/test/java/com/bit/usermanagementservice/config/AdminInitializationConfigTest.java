package com.bit.usermanagementservice.config;

import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.entity.User;
import com.bit.usermanagementservice.exceptions.rolenotfound.RoleNotFoundException;
import com.bit.usermanagementservice.repository.RoleRepository;
import com.bit.usermanagementservice.repository.UserRepository;
import com.bit.usermanagementservice.utils.CredentialsProducer;
import lombok.Getter;
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
@Getter
class AdminInitializationConfigTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoderConfig passwordEncoderConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CredentialsProducer credentialsProducer;

    @InjectMocks
    private AdminInitializationConfig adminInitializationConfig;

    private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

    private Role adminRole;
    private Set<Role> roles;
    private User adminUser;

    @BeforeEach
    public void setup() {
        adminRole = new Role("ROLE_ADMIN");

        roles = new HashSet<>();
        roles.add(adminRole);

        adminUser = new User(
                "admin",
                "admin",
                "admin@gmail.com",
                "admin",
                "password",
                roles
        );
    }

    @Test
    void shouldInitializeAdminIfNoAdminExists() {
        when(roleRepository.findByName(ADMIN_ROLE_NAME)).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRoles(any())).thenReturn(Collections.emptyList());
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotInitializeAdminIfAdminExists() {
        when(roleRepository.findByName(ADMIN_ROLE_NAME)).thenReturn(Optional.of(adminRole));
        when(userRepository.findByRoles(roles)).thenReturn(Collections.singletonList(adminUser));

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReactivateTheInitialAdminIfDeletedBefore(){
        adminUser.setDeleted(true);

        when(roleRepository.findByName(ADMIN_ROLE_NAME)).thenReturn(Optional.of(adminRole));
        when(userRepository.findByUserCode("admin")).thenReturn(Optional.of(adminUser));

        adminInitializationConfig.initializeAdmin();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void throwsErrorWhenAdminRoleDoesntExist(){
        when(roleRepository.findByName(ADMIN_ROLE_NAME)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> adminInitializationConfig.initializeAdmin());
    }
}