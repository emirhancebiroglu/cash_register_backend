package com.bit.user_management_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Set;
import java.util.TreeSet;

@Component
public class UserCodeGenerator {
    private final static Logger logger = LoggerFactory.getLogger(PasswordGenerator.class);
    private static final String ADMIN_ROLE_CODE = "A";
    private static final String CASHIER_ROLE_CODE = "C";
    private static final String STORE_MANAGER_ROLE_CODE = "S";
    private static final int RANDOM_DIGIT_LENGTH = 6;
    private static final String DIGITS = "0123456789";

    public String createUserCode(Set<String> roles, Long userId){
        StringBuilder userCode = new StringBuilder();

        Set<String> sortedRoles = new TreeSet<>(roles);

        for (String role : sortedRoles) {
            switch (role) {
                case "ROLE_ADMIN":
                    userCode.append(ADMIN_ROLE_CODE);
                    break;
                case "ROLE_CASHIER":
                    userCode.append(CASHIER_ROLE_CODE);
                    break;
                case "ROLE_STORE_MANAGER":
                    userCode.append(STORE_MANAGER_ROLE_CODE);
                    break;
                default:
                    break;
            }
        }

        userCode.append(userId);

        userCode.append(generateRandomDigits());

        logger.info("User Code generated: " + userCode);
        return userCode.toString();
    }

    private String generateRandomDigits() {
        StringBuilder randomDigits = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < UserCodeGenerator.RANDOM_DIGIT_LENGTH; i++) {
            randomDigits.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return randomDigits.toString();
    }
}