package com.bit.usermanagementservice.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * The PasswordGenerator class is responsible for generating passwords.
 * It creates passwords based on a combination of user email, user ID, random numbers, and special characters.
 */
@Component
public class PasswordGenerator {
    private static final Logger logger = LogManager.getLogger(PasswordGenerator.class);
    private static final String SPECIAL_CHARACTERS = "!@#*_,.?";

    /**
     * Creates a password based on the user's email and ID, along with random numbers and a special character.
     *
     * @param email the user's email.
     * @param userId the user's ID.
     * @return the generated password.
     */
    public String createPassword(String email, Long userId){
        logger.info("Creating password");

        StringBuilder password = new StringBuilder();

        String emailPrefix = email.substring(0, 3).toUpperCase();
        password.append(emailPrefix);

        password.append(userId);

        password.append(generateRandomNumbers());

        password.append(generateRandomSpecialCharacter());

        logger.info("Password created");
        return password.toString();
    }

    /**
     * Generates a string of random numbers.
     *
     * @return the generated random numbers.
     */
    private String generateRandomNumbers() {
        StringBuilder numbers = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 3; i++) {
            numbers.append(random.nextInt(10));
        }
        return numbers.toString();
    }

    /**
     * Generates a random special character from the defined set of special characters.
     *
     * @return the generated random special character.
     */
    private char generateRandomSpecialCharacter() {
        SecureRandom random = new SecureRandom();
        return SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length()));
    }
}
