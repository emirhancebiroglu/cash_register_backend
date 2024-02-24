package com.bit.user_management_service.config;

import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleInitializationConfigTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializationConfig roleInitializationConfig;

    private List<Role> roles;

    @BeforeEach
    public void setup() {
        roles = new ArrayList<>();
        when(roleRepository.findByName(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return roles.stream().filter(r -> r.getName().equals(name)).findFirst();
        });
    }

    @Test
    public void initializeRoles() {
        roles.add(new Role("ROLE_ADMIN"));
        roles.add(new Role("ROLE_CASHIER"));

        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(any(Role.class));
    }
}
