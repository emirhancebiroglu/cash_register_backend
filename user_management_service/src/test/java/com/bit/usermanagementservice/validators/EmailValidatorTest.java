package com.bit.usermanagementservice.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmailValidatorTest {
    @Test
    void testValidEmail() {
        EmailValidator emailValidator = new EmailValidator();

        String validEmail = "test@example.com";
        assertTrue(emailValidator.isValidEmail(validEmail));
    }

    @Test
    void testInvalidEmail() {
        EmailValidator emailValidator = new EmailValidator();

        String invalidEmail = "invalid_email.com";
        assertFalse(emailValidator.isValidEmail(invalidEmail));
    }
}
