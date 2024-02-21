package com.bit.user_management_service.service;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.dto.UserDto;
import com.bit.user_management_service.exceptions.UserAlreadyExists.UserAlreadyExistsException;
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
    void test_AddUser_With_Initial_Admin_User() {
        UserDto UserDto = new UserDto("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "Emirhan2165",
                Collections.singleton("ROLE_ADMIN"));

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findByUserCode("admin@gmail.com")).thenReturn(Optional.of(new User()));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("Emirhan2165")).thenReturn("encodedPassword");

        userService.addUser(UserDto);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void test_Add_User_Without_Initial_Admin_User() {
        UserDto UserDto = new UserDto("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "Emirhan2165",
                Collections.singleton("ROLE_ADMIN"));

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findByUserCode("admin@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("Emirhan2165")).thenReturn("encodedPassword");

        userService.addUser(UserDto);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    void test_Add_User_With_Existing_User() {
        UserDto UserDto = new UserDto("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "Emirhan2165",
                Collections.singleton("ROLE_ADMIN"));

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findByUserCode(UserDto.getUserCode())).thenReturn(Optional.of(new User()));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode("Emirhan2165")).thenReturn("encodedPassword");

        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(UserDto));
    }

    @Test
    void test_Update_User() throws Exception {
        Role role = new Role("ROLE_ADMIN");

        Long userId = 1L;
        UserDto updatedUserDto = new UserDto("emirhan",
                "cebiroglu",
                "emirhan@hotmail.com",
                "Emirhan2165",
                Collections.singleton(role.getName()));

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .userCode("old.userCode@example.com")
                .password("oldEmirhan2165")
                .roles(Collections.singleton(new Role("ROLE_CASHIER")))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role()));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(mock(PasswordEncoder.class));
        when(passwordEncoderConfig.passwordEncoder().encode(any(CharSequence.class))).thenReturn("encodedPassword");

        userService.updateUser(userId, updatedUserDto);

        verify(userRepository, times(1)).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(updatedUserDto.getFirstName(), capturedUser.getFirstName());
        assertEquals(updatedUserDto.getLastName(), capturedUser.getLastName());
        assertEquals(updatedUserDto.getUserCode(), capturedUser.getUserCode());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(1, capturedUser.getRoles().size());
        assertTrue(capturedUser.getRoles().contains(role));
    }

    @Test
    void test_Delete_Existing_User() {
        Long userId = 1L;

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .userCode("old.userCode@example.com")
                .password("oldEmirhan2165")
                .roles(Collections.singleton(new Role("ROLE_CASHIER"))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).save(existingUser);
        assertTrue(existingUser.isDeleted());
    }

    @Test
    void test_Delete_NonExisting_User() {
        Long userId = 1L;

        User existingUser = User.builder()
                .id(userId)
                .firstName("OldFirstName")
                .lastName("OldLastName")
                .userCode("old.userCode@example.com")
                .password("oldEmirhan2165")
                .roles(Collections.singleton(new Role("ROLE_CASHIER"))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(0)).save(existingUser);
        assertFalse(existingUser.isDeleted());

    }
}