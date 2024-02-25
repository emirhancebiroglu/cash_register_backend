package com.bit.user_management_service.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NameValidatorTest {
    @Test
    public void testValidFirstName() {
        NameValidator nameValidator = new NameValidator();

        String validFirstName = "Emirhan";
        assertTrue(nameValidator.validateFirstName(validFirstName));
    }

    @Test
    public void testInvalidFirstName() {
        NameValidator nameValidator = new NameValidator();

        String invalidFirstName = "Emirhan123";
        assertFalse(nameValidator.validateFirstName(invalidFirstName));
    }

    @Test
    public void testValidLastName() {
        NameValidator nameValidator = new NameValidator();

        String validLastName = "CEBIROGLU";
        assertTrue(nameValidator.validateLastName(validLastName));
    }

    @Test
    public void testInvalidLastName() {
        NameValidator nameValidator = new NameValidator();

        String invalidLastName = "cebiroglu";
        assertFalse(nameValidator.validateLastName(invalidLastName));
    }
}