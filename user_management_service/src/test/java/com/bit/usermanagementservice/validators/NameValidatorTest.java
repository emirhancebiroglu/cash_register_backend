package com.bit.usermanagementservice.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class NameValidatorTest {
    private NameValidator nameValidator;
    @BeforeEach
    void setup(){
        nameValidator = new NameValidator();
    }

    @Test
    void validFirstName() {
        String validFirstName = "Emirhan";
        assertTrue(nameValidator.validateFirstName(validFirstName));
    }

    @Test
    void invalidFirstName() {
        String invalidFirstName = "Emirhan123";
        assertFalse(nameValidator.validateFirstName(invalidFirstName));
    }

    @Test
    void validLastName() {
        String validLastName = "CEBIROGLU";
        assertTrue(nameValidator.validateLastName(validLastName));
    }

    @Test
    void invalidLastName() {
        String invalidLastName = "cebiroglu";
        assertFalse(nameValidator.validateLastName(invalidLastName));
    }

    @Test
    void nullOrEmptyName() {
        String emptyName = "";
        assertFalse(nameValidator.validateFirstName(emptyName));
    }

    @Test
    void whenFirstNameDoesntStartWithAnUpperCase() {
        String firstName = "emirhan";
        assertFalse(nameValidator.validateFirstName(firstName));
    }
}