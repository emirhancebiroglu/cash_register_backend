package com.bit.user_management_service.service;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.config.AdminInitializationConfig;
import com.bit.user_management_service.dto.AddUser.AddUserReq;
import com.bit.user_management_service.dto.UpdateUser.UpdateUserReq;
import com.bit.user_management_service.service.serviceImpl.UserServiceImpl;
import com.bit.user_management_service.utils.PasswordGenerator;
import com.bit.user_management_service.utils.UserCodeGenerator;
import com.bit.user_management_service.validators.EmailValidator;
import com.bit.user_management_service.validators.NameValidator;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Getter
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoderConfig passwordEncoderConfig;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    PasswordGenerator passwordGenerator;

    @Mock
    UserCodeGenerator userCodeGenerator;

    @Mock
    NameValidator nameValidator;

    @Mock
    EmailValidator emailValidator;

    @Mock
    EmailService emailService;

    @Mock
    AdminInitializationConfig adminInitializationConfig;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void addUser() {
        AddUserReq addUserReq = new AddUserReq();
        addUserReq.setEmail("emirhan@hotmail.com");
        addUserReq.setFirstName("emirhan");
        addUserReq.setLastName("cebiroglu");

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        addUserReq.setRoles(roles);

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateFirstName(addUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(addUserReq.getLastName())).thenReturn(true);

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role("ROLE_ADMIN")));

        when(userCodeGenerator.createUserCode(addUserReq.getRoles(), 1L)).thenReturn("TEST123456");
        when(passwordGenerator.createPassword(addUserReq.getEmail(), 1L)).thenReturn("test_password");

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("test_password")).thenReturn("encoded_password");

        userService.addUser(addUserReq);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        Long userId = 1L;

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        Set<Role> existingRoles = new HashSet<>();
        existingRoles.add(new Role("ROLE_ADMIN"));

        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setFirstName("newFirstName");
        updateUserReq.setLastName("newLastName");
        updateUserReq.setEmail("new.email@example.com");
        updateUserReq.setRoles(roles);

        User existingUser = User.builder()
                .id(userId)
                .firstName("oldFirstName")
                .lastName("oldLasName")
                .email("old.email@example.com")
                .userCode("AS12356890")
                .password("NEW1345.")
                .roles(existingRoles)
                .build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role("ROLE_ADMIN")));

        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(updateUserReq.getLastName())).thenReturn(true);
        when(emailValidator.isValidEmail(updateUserReq.getEmail())).thenReturn(true);

        userService.updateUser(userId, updateUserReq);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(updateUserReq.getFirstName(), existingUser.getFirstName());
        assertEquals(updateUserReq.getLastName(), existingUser.getLastName());
        assertEquals(updateUserReq.getEmail(), existingUser.getEmail());
    }

    @Test
    void deleteUser(){
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(userId);

        assertTrue(existingUser.isDeleted());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void reactivateUser(){
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDeleted(true);
        existingUser.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(passwordGenerator.createPassword(existingUser.getEmail(), 1L)).thenReturn("test_password");

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("test_password")).thenReturn("encoded_password");

        userService.reactivateUser(userId);

        assertFalse(existingUser.isDeleted());

        verify(userRepository, times(1)).save(existingUser);
    }
}
