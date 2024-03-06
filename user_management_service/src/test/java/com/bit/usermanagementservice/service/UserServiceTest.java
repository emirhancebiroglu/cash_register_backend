package com.bit.usermanagementservice.service;

import com.bit.usermanagementservice.config.AdminInitializationConfig;
import com.bit.usermanagementservice.config.PasswordEncoderConfig;
import com.bit.usermanagementservice.dto.AddUser.AddUserReq;
import com.bit.usermanagementservice.dto.UpdateUser.UpdateUserReq;
import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.entity.User;
import com.bit.usermanagementservice.exceptions.InvalidEmail.InvalidEmailException;
import com.bit.usermanagementservice.exceptions.InvalidName.InvalidNameException;
import com.bit.usermanagementservice.exceptions.UserAlreadyActive.UserAlreadyActiveException;
import com.bit.usermanagementservice.exceptions.UserAlreadyDeleted.UserAlreadyDeletedException;
import com.bit.usermanagementservice.exceptions.UserAlreadyExists.UserAlreadyExistsException;
import com.bit.usermanagementservice.exceptions.UserNotFound.UserNotFoundException;
import com.bit.usermanagementservice.repository.RoleRepository;
import com.bit.usermanagementservice.repository.UserRepository;
import com.bit.usermanagementservice.service.serviceimpl.UserServiceImpl;
import com.bit.usermanagementservice.utils.CredentialsProducer;
import com.bit.usermanagementservice.utils.PasswordGenerator;
import com.bit.usermanagementservice.utils.UserCodeGenerator;
import com.bit.usermanagementservice.validators.EmailValidator;
import com.bit.usermanagementservice.validators.NameValidator;
import lombok.Data;
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
@Data
class UserServiceTest {
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

    @Mock
    CredentialsProducer credentialsProducer;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void addUser_WithAdminRole_HandlesInitialAdmin() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        AddUserReq addUserReq = new AddUserReq();
        addUserReq.setEmail("emirhan@hotmail.com");
        addUserReq.setFirstName("emirhan");
        addUserReq.setLastName("cebiroglu");
        addUserReq.setRoles(roles);

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateFirstName(addUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(addUserReq.getLastName())).thenReturn(true);

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role("ROLE_ADMIN")));
        when(userRepository.findByUserCode("admin")).thenReturn(Optional.of(new User()));
        when(userRepository.findMaxId()).thenReturn(null);

        when(userCodeGenerator.createUserCode(addUserReq.getRoles(), 2L)).thenReturn("TEST123456");
        when(passwordGenerator.createPassword(addUserReq.getEmail(), 2L)).thenReturn("test_password");

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("test_password")).thenReturn("encoded_password");

        userService.addUser(addUserReq);

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void addUser_WithExistingUser_ThrowsUserAlreadyExistsException() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        AddUserReq addUserReq = new AddUserReq();
        addUserReq.setEmail("emirhan@hotmail.com");
        addUserReq.setFirstName("emirhan");
        addUserReq.setLastName("cebiroglu");
        addUserReq.setRoles(roles);

        when(userRepository.findByEmail(addUserReq.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(addUserReq));
    }

    @Test
    void updateUser_IfUserIsNotDeleted() {
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
    void updateUser_IfUserIsDeleted() {
        Long userId = 1L;

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        Set<Role> existingRoles = new HashSet<>();
        existingRoles.add(new Role("ROLE_ADMIN"));

        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setFirstName("NewFirstName");
        updateUserReq.setLastName("NEWLASTNAME");
        updateUserReq.setEmail("new.email@example.com");
        updateUserReq.setRoles(roles);

        User existingUser = User.builder()
                .id(userId)
                .firstName("oldFirstName")
                .lastName("oldLastName")
                .email("old.email@example.com")
                .userCode("AS12356890")
                .password("NEW1345.")
                .roles(existingRoles)
                .isDeleted(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updateUserReq));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidEmail_ThrowsInvalidEmailException() {
        Long userId = 1L;

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setFirstName("newFirstName");
        updateUserReq.setLastName("newLastName");
        updateUserReq.setEmail("invalidemail");
        updateUserReq.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(updateUserReq.getLastName())).thenReturn(true);

        assertThrows(InvalidEmailException.class, () -> userService.updateUser(userId, updateUserReq));
    }

    @Test
    void updateUser_WithInvalidLastName_ThrowsInvalidLastNameException() {
        Long userId = 1L;

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setFirstName("newFirstName");
        updateUserReq.setLastName("newLastName");
        updateUserReq.setEmail("new.email@example.com");
        updateUserReq.setRoles(roles);

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);

        assertThrows(InvalidNameException.class, () -> userService.updateUser(userId, updateUserReq));
    }

    @Test
    void updateUser_WithInvalidFirstName_ThrowsInvalidFirstNameException() {
        Long userId = 1L;

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");

        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setFirstName("newFirstName");
        updateUserReq.setLastName("newLastName");
        updateUserReq.setEmail("new.email@example.com");
        updateUserReq.setRoles(roles);

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(InvalidNameException.class, () -> userService.updateUser(userId, updateUserReq));
    }

    @Test
    void updateUser_IfRolesAreChanged() {
        Long userId = 1L;

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CASHIER");

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
        when(roleRepository.findByName("ROLE_CASHIER")).thenReturn(Optional.of(new Role("ROLE_CASHIER")));

        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(updateUserReq.getLastName())).thenReturn(true);
        when(emailValidator.isValidEmail(updateUserReq.getEmail())).thenReturn(true);

        when(userCodeGenerator.createUserCode(roles, userId)).thenReturn("C1456754");


        userService.updateUser(userId, updateUserReq);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(updateUserReq.getFirstName(), existingUser.getFirstName());
        assertEquals(updateUserReq.getLastName(), existingUser.getLastName());
        assertEquals(updateUserReq.getEmail(), existingUser.getEmail());
    }


    @Test
    void deleteUser_IfUserIsNotDeleted() {
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
    void deleteUser_IfUserIsDeleted() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyDeletedException.class, () -> userService.deleteUser(userId));

        verify(userRepository, never()).save(existingUser);
    }

    @Test
    void reactivateUser_IfUserNotActive() {
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

    @Test
    void reactivateUser_IfUserIsActive() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyActiveException.class, () -> userService.reactivateUser(userId));
    }
}
