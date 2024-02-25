package com.bit.user_management_service.config;

import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleInitializationConfigTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializationConfig roleInitializationConfig;

    @Test
    public void shouldInitializeRoles() {
        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(3)).save(any(Role.class));
    }
}