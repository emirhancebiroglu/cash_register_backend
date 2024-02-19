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

import java.util.Collections;
import java.util.Optional;

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
    void testAddUserWithInitialAdminUser() {
        UserDTO userDTO = new UserDTO("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "emirhan",
                Collections.singleton("ROLE_ADMIN"));

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(new User()));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("password")).thenReturn("encodedPassword");

        userService.addUser(userDTO);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testAddUserWithoutInitialAdminUser() {
        UserDTO userDTO = new UserDTO("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "emirhan",
                Collections.singleton("ROLE_ADMIN"));

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("password")).thenReturn("encodedPassword");

        userService.addUser(userDTO);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        Role role = new Role("ROLE_ADMIN");

        Long userId = 1L;
        UserDTO updatedUserDTO = new UserDTO("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "emirhan",
                Collections.singleton(role.getName()));

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .email("old.email@example.com")
                .password("oldPassword")
                .roles(Collections.singleton(new Role("ROLE_CASHIER")))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role()));
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
        assertTrue(capturedUser.getRoles().contains(role));
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
                .roles(Collections.singleton(new Role("ROLE_CASHIER"))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).save(existingUser);
        assertTrue(existingUser.isDeleted());
    }

    @Test
    void testDeleteNonExistingUser() {
        Long userId = 1L;

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .email("old.email@example.com")
                .password("oldPassword")
                .roles(Collections.singleton(new Role("ROLE_CASHIER"))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(0)).save(existingUser);
        assertFalse(existingUser.isDeleted());

    }
}