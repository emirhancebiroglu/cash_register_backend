package com.bit.user_management_service.validators;

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
            logger.error("ERROR: Invalid " + type + " name");
            return false;
        }

        if (!name.matches(NAME_REGEX)) {
            logger.error("ERROR: Invalid " + type + " name format");
            return false;
        }

        if (type.equals("first") && !Character.isUpperCase(name.charAt(0))) {
            logger.error("ERROR: First name should start with an uppercase letter");
            return false;
        }

        if (type.equals("last") && !name.equals(name.toUpperCase())) {
            logger.error("ERROR: Last name should be all uppercase letters");
            return false;
        }

        logger.info("INFO: Valid " + type + " name");
        return true;
    }
}