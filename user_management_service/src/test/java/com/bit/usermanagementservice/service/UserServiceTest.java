package com.bit.usermanagementservice.service;

import com.bit.usermanagementservice.config.AdminInitializationConfig;
import com.bit.usermanagementservice.config.PasswordEncoderConfig;
import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.dto.getuser.UserDTO;
import com.bit.usermanagementservice.dto.updateuser.UpdateUserReq;
import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.entity.User;
import com.bit.usermanagementservice.exceptions.invalidemail.InvalidEmailException;
import com.bit.usermanagementservice.exceptions.invalidname.InvalidNameException;
import com.bit.usermanagementservice.exceptions.rolenotfound.RoleNotFoundException;
import com.bit.usermanagementservice.exceptions.useralreadyactive.UserAlreadyActiveException;
import com.bit.usermanagementservice.exceptions.useralreadydeleted.UserAlreadyDeletedException;
import com.bit.usermanagementservice.exceptions.useralreadyexists.UserAlreadyExistsException;
import com.bit.usermanagementservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.usermanagementservice.repository.RoleRepository;
import com.bit.usermanagementservice.repository.UserRepository;
import com.bit.usermanagementservice.service.serviceimpl.UserServiceImpl;
import com.bit.usermanagementservice.utils.CredentialsProducer;
import com.bit.usermanagementservice.utils.PasswordGenerator;
import com.bit.usermanagementservice.utils.UserCodeGenerator;
import com.bit.usermanagementservice.validators.EmailValidator;
import com.bit.usermanagementservice.validators.NameValidator;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

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

    private AddUserReq addUserReq;
    private UpdateUserReq updateUserReq;
    private Set<String> roles = new HashSet<>();
    private Set<Role> existingRoles = new HashSet<>();
    private User existingUser;
    private Long userId;
    private List<User> userList;

    @BeforeEach
    void setup(){
        roles.add("ROLE_ADMIN");
        existingRoles.add(new Role("ROLE_ADMIN"));
        userId = 1L;

        addUserReq = new AddUserReq(
                "emirhan@hotmail.com",
                "emirhan",
                "cebiroglu",
                roles
        );

        updateUserReq = new UpdateUserReq(
                "newFirstName",
                "newLastName",
                "new.email@example.com",
                roles
        );

        existingUser = new User(
                "oldFirstName",
                "oldLasName",
                "old.email@example.com",
                "AS12356890",
                "NEW1345.",
                existingRoles
        );

        userList = new ArrayList<>();
        userList.add(new User(
                "John",
                "Doe",
                "<EMAIL>",
                "C1456754",
                "encoded_password",
                Collections.singleton(new Role("ROLE_ADMIN"))
        ));
        userList.add(new User(
                "Alice",
                "Smith",
                "<EMAIL>",
                "C1456754",
                "encoded_password",
                Collections.singleton(new Role("ROLE_ADMIN"))
        ));
    }

    @Test
    void addUser_WithAdminRole_HandlesInitialAdmin() {

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
        when(userRepository.findByEmail(addUserReq.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.addUser(addUserReq));
    }

    @Test
    void addUser_WithNonExistingRole_ThrowsRoleNotFoundException() {
        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateFirstName(addUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(addUserReq.getLastName())).thenReturn(true);

        assertThrows(RoleNotFoundException.class, () -> userService.addUser(addUserReq));
    }

    @Test
    void updateUser_IfUserIsNotDeleted() {
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
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updateUserReq));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidEmail_ThrowsInvalidEmailException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(updateUserReq.getLastName())).thenReturn(true);

        assertThrows(InvalidEmailException.class, () -> userService.updateUser(userId, updateUserReq));
    }

    @Test
    void updateUser_WithInvalidLastName_ThrowsInvalidLastNameException() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);

        assertThrows(InvalidNameException.class, () -> userService.updateUser(userId, updateUserReq));
    }

    @Test
    void updateUser_WithInvalidFirstName_ThrowsInvalidFirstNameException() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(InvalidNameException.class, () -> userService.updateUser(userId, updateUserReq));
    }

    @Test
    void updateUser_IfRolesAreChanged() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_CASHIER"));

        existingUser.setId(userId);
        existingUser.setRoles(roles);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role("ROLE_ADMIN")));

        when(nameValidator.validateFirstName(updateUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(updateUserReq.getLastName())).thenReturn(true);
        when(emailValidator.isValidEmail(updateUserReq.getEmail())).thenReturn(true);

        when(userCodeGenerator.createUserCode(Collections.singleton("ROLE_ADMIN"), userId)).thenReturn("C1456754");

        userService.updateUser(userId, updateUserReq);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(updateUserReq.getFirstName(), existingUser.getFirstName());
        assertEquals(updateUserReq.getLastName(), existingUser.getLastName());
        assertEquals(updateUserReq.getEmail(), existingUser.getEmail());
    }

    @Test
    void deleteUser_IfUserIsNotDeleted() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(userId);

        assertTrue(existingUser.isDeleted());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deleteUser_IfUserIsDeleted() {
        existingUser.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyDeletedException.class, () -> userService.deleteUser(userId));

        verify(userRepository, never()).save(existingUser);
    }

    @Test
    void reactivateUser_IfUserNotActive() {
        existingUser.setDeleted(true);
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(passwordGenerator.createPassword(existingUser.getEmail(), userId)).thenReturn("test_password");

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("test_password")).thenReturn("encoded_password");

        userService.reactivateUser(userId);

        assertFalse(existingUser.isDeleted());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void reactivateUser_IfUserIsActive() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyActiveException.class, () -> userService.reactivateUser(userId));
    }

    @Test
    void testGetUsers() {
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findByisDeletedFalse(any(PageRequest.class))).thenReturn(userPage);

        List<UserDTO> userDTOList = userService.getUsers(0, 10);

        verify(userRepository, times(1)).findByisDeletedFalse(any(PageRequest.class));
        assertEquals(2, userDTOList.size());
        assertEquals("John", userDTOList.get(0).getFirstName());
        assertEquals("Doe", userDTOList.get(0).getLastName());
        assertEquals("Alice", userDTOList.get(1).getFirstName());
        assertEquals("Smith", userDTOList.get(1).getLastName());
    }

    @Test
    void testGetDeletedUsers() {
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findByisDeletedTrue(any(PageRequest.class))).thenReturn(userPage);

        List<UserDTO> userDTOList = userService.getDeletedUsers(0, 10);

        verify(userRepository, times(1)).findByisDeletedTrue(any(PageRequest.class));
        assertEquals(2, userDTOList.size());
        assertEquals("John", userDTOList.get(0).getFirstName());
        assertEquals("Doe", userDTOList.get(0).getLastName());
        assertEquals("Alice", userDTOList.get(1).getFirstName());
        assertEquals("Smith", userDTOList.get(1).getLastName());
    }

    @Test
    void testSearchUserByName() {
        String nameWithSpace = "John Doe";
        String firstNamePrefix = "John";
        String lastNamePrefix = "Doe";

        Page<User> userPageWithNameSpace = new PageImpl<>(userList);

        when(userRepository.findByFirstNameStartingWithIgnoreCaseAndLastNameStartingWithIgnoreCase(
                eq(firstNamePrefix),
                eq(lastNamePrefix),
                any(Pageable.class))
        ).thenReturn(userPageWithNameSpace);

        List<UserDTO> userDTOListWithNameSpace = userService.searchUserByName(nameWithSpace, 0, 10);

        assertEquals(userList.size(), userDTOListWithNameSpace.size());
    }

    @Test
    void testSearchUserByName_OnlyFirstNameOrLastName() {
        String name = "John";
        int pageNo = 0;
        int pageSize = 10;

        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCase(
                eq(name),
                eq(name),
                any(Pageable.class))
        ).thenReturn(userPage);

        List<UserDTO> userDTOList = userService.searchUserByName(name, pageNo, pageSize);

        assertEquals(userList.size(), userDTOList.size());
    }

}
