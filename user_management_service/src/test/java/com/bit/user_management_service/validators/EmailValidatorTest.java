package com.bit.user_management_service.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EmailValidatorTest {
    @Test
    public void testValidEmail() {
        EmailValidator emailValidator = new EmailValidator();

        String validEmail = "test@example.com";
        assertTrue(emailValidator.isValidEmail(validEmail));
    }

    @Test
    public void testInvalidEmail() {
        EmailValidator emailValidator = new EmailValidator();

        String invalidEmail = "invalid_email.com";
        assertFalse(emailValidator.isValidEmail(invalidEmail));
    }
}
