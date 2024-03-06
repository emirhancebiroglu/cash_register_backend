package com.bit.usermanagementservice.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NameValidator {
    private static final Logger logger = LoggerFactory.getLogger(NameValidator.class);
    private static final String NAME_REGEX = "^[a-zA-Z]{3,18}$";

    public boolean validateFirstName(String firstName) {
        return isValidName(firstName, "first");
    }

    public boolean validateLastName(String lastName) {
        return isValidName(lastName, "last");
    }

    private boolean isValidName(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            logger.error("Invalid " + type + " name: Name is null or empty");
            return false;
        }

        if (!name.matches(NAME_REGEX)) {
            logger.error("Invalid " + type + " name format: Name does not match the required pattern");
            return false;
        }

        if (type.equals("first") && !Character.isUpperCase(name.charAt(0))) {
            logger.error("First name should start with an uppercase letter: " + name);
            return false;
        }

        if (type.equals("last") && !name.equals(name.toUpperCase())) {
            logger.error("Last name should be all uppercase letters: " + name);
            return false;
        }

        logger.info("Valid " + type + " name: " + name);
        return true;
    }
}