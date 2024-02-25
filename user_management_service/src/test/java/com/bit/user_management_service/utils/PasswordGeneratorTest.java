package com.bit.user_management_service.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PasswordGeneratorTest {
    @Test
    public void createPassword(){
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String email = "example@example.com";
        Long userId = 12345L;

        String password = passwordGenerator.createPassword(email, userId);

        assertNotNull(password);
        assertTrue(password.length() >= 7);
        assertTrue(password.startsWith("EXA12345"));

        char lastCharacter = password.charAt(password.length() - 1);
        String specialCharacters = "!@#*_,.?";

        assertTrue(specialCharacters.contains(String.valueOf(lastCharacter)));
    }

}
