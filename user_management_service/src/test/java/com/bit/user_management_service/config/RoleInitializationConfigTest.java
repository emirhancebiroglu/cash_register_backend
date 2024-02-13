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
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CASHIER")).thenReturn(Optional.of(new Role("CASHIER")));
        when(roleRepository.findByName("STORE-MANAGER")).thenReturn(Optional.of(new Role("STORE-MANAGER")));

        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(new Role("ADMIN"));
        verify(roleRepository, times(0)).save(new Role("CASHIER"));
        verify(roleRepository, times(0)).save(new Role("STORE-MANAGER"));
    }

    @Test
    public void testWhenAdminAndStoreManagerNotExist(){
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CASHIER")).thenReturn(Optional.of(new Role("CASHIER")));
        when(roleRepository.findByName("STORE-MANAGER")).thenReturn(Optional.empty());


        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(new Role("ADMIN"));
        verify(roleRepository, times(0)).save(new Role("CASHIER"));
        verify(roleRepository, times(1)).save(new Role("STORE-MANAGER"));
    }

    @Test
    public void testWhenNoRoleExists(){
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CASHIER")).thenReturn(Optional.empty());
        when(roleRepository.findByName("STORE-MANAGER")).thenReturn(Optional.empty());

        roleInitializationConfig.initializeRoles();

        verify(roleRepository, times(1)).save(new Role("ADMIN"));
        verify(roleRepository, times(1)).save(new Role("CASHIER"));
        verify(roleRepository, times(1)).save(new Role("STORE-MANAGER"));
    }
}
