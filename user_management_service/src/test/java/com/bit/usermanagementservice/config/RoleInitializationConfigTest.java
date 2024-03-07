package com.bit.usermanagementservice.config;

import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleInitializationConfigTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializationConfig roleInitializationConfig;

    @Test
    void shouldInitializeRoles() {
        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(3)).save(any(Role.class));
    }

    @Test
    void shouldSkipInitializingRoles() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(2)).save(any(Role.class));
    }
}