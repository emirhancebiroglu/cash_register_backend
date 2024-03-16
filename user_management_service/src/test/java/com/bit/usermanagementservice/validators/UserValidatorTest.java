package com.bit.usermanagementservice.validators;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.exceptions.atleastoneroleneeded.AtLeastOneRoleNeededException;
import com.bit.usermanagementservice.exceptions.invalidemail.InvalidEmailException;
import com.bit.usermanagementservice.exceptions.invalidname.InvalidNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserValidatorTest {

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private NameValidator nameValidator;

    @InjectMocks
    private UserValidator userValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateUserData_WithValidData_ShouldNotThrowException() {
        AddUserReq addUserReq = new AddUserReq("test@example.com", "John", "Doe", Collections.singleton("ROLE_ADMIN"));

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateFirstName(addUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(addUserReq.getLastName())).thenReturn(true);

        userValidator.validateUserData(addUserReq);
    }

    @Test
    void validateUserData_WithInvalidEmail_ShouldThrowInvalidEmailException() {
        AddUserReq addUserReq = new AddUserReq("invalid_email", "John", "Doe", Collections.singleton("ROLE_ADMIN"));

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(false);

        assertThrows(InvalidEmailException.class, () -> userValidator.validateUserData(addUserReq));
    }

    @Test
    void validateUserData_WithEmptyFirstName_ShouldThrowInvalidNameException() {
        AddUserReq addUserReq = new AddUserReq("test@example.com", "", "Doe", Collections.singleton("ROLE_ADMIN"));

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateLastName(addUserReq.getLastName())).thenReturn(true);

        assertThrows(InvalidNameException.class, () -> userValidator.validateUserData(addUserReq));
    }

    @Test
    void validateUserData_WithEmptyLastName_ShouldThrowInvalidNameException() {
        AddUserReq addUserReq = new AddUserReq("test@example.com", "John", "", Collections.singleton("ROLE_ADMIN"));

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateFirstName(addUserReq.getFirstName())).thenReturn(true);

        assertThrows(InvalidNameException.class, () -> userValidator.validateUserData(addUserReq));
    }

    @Test
    void validateUserData_WithNoRoles_ShouldThrowAtLeastOneRoleNeededException() {
        AddUserReq addUserReq = new AddUserReq("test@example.com", "John", "Doe", new HashSet<>());

        when(emailValidator.isValidEmail(addUserReq.getEmail())).thenReturn(true);
        when(nameValidator.validateFirstName(addUserReq.getFirstName())).thenReturn(true);
        when(nameValidator.validateLastName(addUserReq.getLastName())).thenReturn(true);

        assertThrows(AtLeastOneRoleNeededException.class, () -> userValidator.validateUserData(addUserReq));
    }
}