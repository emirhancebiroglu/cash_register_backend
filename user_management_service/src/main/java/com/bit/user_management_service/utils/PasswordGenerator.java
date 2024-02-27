package com.bit.user_management_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {
    private final static Logger logger = LoggerFactory.getLogger(PasswordGenerator.class);
    private static final String SPECIAL_CHARACTERS = "!@#*_,.?";

    public String createPassword(String email, Long userId){
        StringBuilder password = new StringBuilder();

        String emailPrefix = email.substring(0, 3).toUpperCase();
        password.append(emailPrefix);

        password.append(userId);

        password.append(generateRandomNumbers());

        password.append(generateRandomSpecialCharacter());

        logger.info("Generated Password for User with ID " + userId + ": " + password);
        return password.toString();
    }

    private String generateRandomNumbers() {
        StringBuilder numbers = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 3; i++) {
            numbers.append(random.nextInt(10));
        }
        return numbers.toString();
    }

    private char generateRandomSpecialCharacter() {
        SecureRandom random = new SecureRandom();
        return SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length()));
    }
}
