package com.bit.user_management_service.config;

import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class RoleInitializationConfigTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleInitializationConfig roleInitializationConfig;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testWhenAdminNotExist(){
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role("ROLE_CASHIER")));
        when(roleRepository.findByName("ROLE_STORE-MANAGER")).thenReturn(Optional.of(new Role("ROLE_STORE-MANAGER")));

        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(new Role("ROLE_ADMIN"));
        verify(roleRepository, times(0)).save(new Role("ROLE_CASHIER"));
        verify(roleRepository, times(0)).save(new Role("ROLE_STORE-MANAGER"));
    }

    @Test
    public void testWhenAdminAndStoreManagerNotExist(){
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role("ROLE_CASHIER")));
        when(roleRepository.findByName("ROLE_STORE-MANAGER")).thenReturn(Optional.empty());


        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(new Role("ROLE_ADMIN"));
        verify(roleRepository, times(0)).save(new Role("ROLE_CASHIER"));
        verify(roleRepository, times(1)).save(new Role("ROLE_STORE-MANAGER"));
    }

    @Test
    public void testWhenNoRoleExists(){
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_STORE-MANAGER")).thenReturn(Optional.empty());

        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(new Role("ROLE_ADMIN"));
        verify(roleRepository, times(1)).save(new Role("ROLE_CASHIER"));
        verify(roleRepository, times(1)).save(new Role("ROLE_STORE-MANAGER"));
    }
}
