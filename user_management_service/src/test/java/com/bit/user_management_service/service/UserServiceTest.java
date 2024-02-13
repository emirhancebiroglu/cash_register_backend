package com.bit.user_management_service.service;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoderConfig passwordEncoderConfig;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUserWithAdminRole() {
        Role adminRole = new Role("ADMIN");
        adminRole.setId(1L);

        UserDTO userDTO = new UserDTO("Emirhan",
                "Cebiroglu",
                "emirhancebiroglu21@gmail.com",
                "Emirhan2165",
                Collections.singleton("ADMIN"));

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("password")).thenReturn("encodedPassword");

        userService.addUser(userDTO);
    }

    @Test
    void testAddUserWithoutAdminRole() {
        Set<String> roleNames = new HashSet<>(Arrays.asList("CASHIER", "STORE-MANAGER"));

        Role adminRole = new Role("ADMIN");
        adminRole.setId(1L);
        Role cashierRole = new Role("CASHIER");
        cashierRole.setId(2L);
        Role storeManagerRole = new Role("STORE-MANAGER");
        storeManagerRole.setId(2L);

        UserDTO userDTO = new UserDTO("Emirhan",
                "Cebiroglu",
                "emirhancebiroglu21@hotmail.com",
                "Emirhan2165",
                roleNames);

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("CASHIER")).thenReturn(Optional.of(cashierRole));
        when(roleRepository.findByName("STORE-MANAGER")).thenReturn(Optional.of(storeManagerRole));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("password")).thenReturn("encodedPassword");

        userService.addUser(userDTO);
    }

    @Test
    void testAddUserWithAdminRoleWithoutInitialAdminUser() {
        Role adminRole = new Role("ADMIN");
        adminRole.setId(1L);

        UserDTO userDTO = new UserDTO("Emirhan",
                "Cebiroglu",
                "emirhancebiroglu21@gmail.com",
                "Emirhan2165",
                Collections.singleton("ADMIN"));

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("password")).thenReturn("encodedPassword");

        userService.addUser(userDTO);
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UserDTO updatedUserDTO = new UserDTO("Emirhan",
                "Cebiroglu",
                "emirhancebiroglu21@hotmail.com",
                "Emirhan2165",
                Collections.singleton("ADMIN"));

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .email("old.email@example.com")
                .password("oldPassword")
                .roles(Collections.singleton(new Role("CASHIER"))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        Role adminRole = new Role("ADMIN");
        adminRole.setId(1L);
        Role cashierRole = new Role("CASHIER");
        cashierRole.setId(2L);

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("CASHIER")).thenReturn(Optional.of(cashierRole));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode(any(CharSequence.class))).thenReturn("encodedPassword");

        userService.updateUser(userId, updatedUserDTO);

        verify(userRepository, times(1)).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(updatedUserDTO.getFirstName(), capturedUser.getFirstName());
        assertEquals(updatedUserDTO.getLastName(), capturedUser.getLastName());
        assertEquals(updatedUserDTO.getEmail(), capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(1, capturedUser.getRoles().size());
        assertTrue(capturedUser.getRoles().contains(adminRole));
    }

    @Test
    void testDeleteExistingUser() {
        Long userId = 1L;

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .email("old.email@example.com")
                .password("oldPassword")
                .roles(Collections.singleton(new Role("CASHIER"))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).save(existingUser);
        assertTrue(existingUser.isDeleted());
    }

    @Test
    void testDeleteNonExistingUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
    }
}