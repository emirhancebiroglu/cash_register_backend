package com.bit.usermanagementservice.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmailValidatorTest {
    private EmailValidator emailValidator;
    @BeforeEach
    void setup(){
        emailValidator = new EmailValidator();
    }

    @Test
    void testValidEmail() {
        String validEmail = "test@example.com";
        assertTrue(emailValidator.isValidEmail(validEmail));
    }

    @Test
    void testInvalidEmail() {
        String invalidEmail = "invalid_email.com";
        assertFalse(emailValidator.isValidEmail(invalidEmail));
    }
}
