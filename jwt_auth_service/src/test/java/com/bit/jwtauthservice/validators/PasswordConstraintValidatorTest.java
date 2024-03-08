package com.bit.jwtauthservice.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PasswordConstraintValidatorTest {
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @InjectMocks
    PasswordConstraintValidator passwordConstraintValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }

    @Test
    void isValid_validPassword() {
        String validPassword = "Test@12345";
        boolean isValid = passwordConstraintValidator.isValid(validPassword, constraintValidatorContext);
        assertTrue(isValid);
    }

    @Test
    void isValid_invalidPassword() {
        String invalidPassword = "invalidpassword";
        boolean isValid = passwordConstraintValidator.isValid(invalidPassword, constraintValidatorContext);
        assertFalse(isValid);
    }

    @Test
    void isValid_passwordTooShort() {
        String shortPassword = "Short1";

        boolean isValid = passwordConstraintValidator.isValid(shortPassword, constraintValidatorContext);

        assertFalse(isValid);
    }

    @Test
    void isValid_passwordTooLong() {
        String longPassword = "ThisIsAReallyLongPasswordThatExceedsTheMaxLengthLimit1234";

        boolean isValid = passwordConstraintValidator.isValid(longPassword, constraintValidatorContext);

        assertFalse(isValid);
    }

    @Test
    void isValid_passwordNoUpperCase() {
        String noUpperCasePassword = "test@12345";

        boolean isValid = passwordConstraintValidator.isValid(noUpperCasePassword, constraintValidatorContext);

        assertFalse(isValid);
    }

    @Test
    void isValid_passwordNoLowerCase() {
        String noLowerCasePassword = "TEST@12345";

        boolean isValid = passwordConstraintValidator.isValid(noLowerCasePassword, constraintValidatorContext);

        assertFalse(isValid);
    }

    @Test
    void isValid_passwordNoDigit() {
        String noDigitPassword = "Test@Password";

        boolean isValid = passwordConstraintValidator.isValid(noDigitPassword, constraintValidatorContext);

        assertFalse(isValid);
    }

    @Test
    void isValid_passwordWithWhitespace() {
        String whitespacePassword = "Test @12345";

        boolean isValid = passwordConstraintValidator.isValid(whitespacePassword, constraintValidatorContext);

        assertFalse(isValid);
    }
}