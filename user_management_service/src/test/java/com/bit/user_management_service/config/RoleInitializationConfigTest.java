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
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitializeRoles_WhenAdminNotExist() {
        // Given
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role("ROLE_CASHIER")));
        when(roleRepository.findByName("ROLE_STORE_MANAGER")).thenReturn(Optional.of(new Role("ROLE_STORE_MANAGER")));

        // When
        roleInitializationConfig.initializeRoles();

        // Then
        verify(roleRepository, times(1)).save(new Role("ROLE_ADMIN"));
        verify(roleRepository, never()).save(new Role("ROLE_CASHIER"));
        verify(roleRepository, never()).save(new Role("ROLE_STORE_MANAGER"));
    }

    @Test
    public void testInitializeRoles_WhenAdminAndStoreManagerNotExist() {
        // Given
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role("ROLE_CASHIER")));
        when(roleRepository.findByName("ROLE_STORE_MANAGER")).thenReturn(Optional.empty());

        // When
        roleInitializationConfig.initializeRoles();

        // Then
        verify(roleRepository, times(1)).save(new Role("ROLE_ADMIN"));
        verify(roleRepository, never()).save(new Role("ROLE_CASHIER"));
        verify(roleRepository, times(1)).save(new Role("ROLE_STORE_MANAGER"));
    }

    @Test
    public void testInitializeRoles_WhenNoRoleExists() {
        // Given
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_STORE_MANAGER")).thenReturn(Optional.empty());

        // When
        roleInitializationConfig.initializeRoles();

        // Then
        verify(roleRepository, times(1)).save(new Role("ROLE_ADMIN"));
        verify(roleRepository, times(1)).save(new Role("ROLE_CASHIER"));
        verify(roleRepository, times(1)).save(new Role("ROLE_STORE_MANAGER"));
    }
}
