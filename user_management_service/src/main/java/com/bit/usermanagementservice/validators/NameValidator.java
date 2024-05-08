package com.bit.usermanagementservice.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * The NameValidator class is responsible for validating first and last names.
 * It checks whether a name matches the specified format and conventions.
 * It logs validation results using SLF4J logger.
 */
@Component
public class NameValidator {
    private static final Logger logger = LogManager.getLogger(NameValidator.class);
    private static final String NAME_REGEX = "^[a-zA-Z]{3,18}$";

    /**
     * Validates a first name.
     *
     * @param firstName the first name to validate.
     * @return true if the first name is valid, false otherwise.
     */
    public boolean validateFirstName(String firstName) {
        return isValidName(firstName, "first");
    }

    /**
     * Validates a last name.
     *
     * @param lastName the last name to validate.
     * @return true if the last name is valid, false otherwise.
     */
    public boolean validateLastName(String lastName) {
        return isValidName(lastName, "last");
    }

    /**
     * Validates a name (either first name or last name) based on its type.
     *
     * @param name the name to validate.
     * @param type the type of the name ("first" or "last").
     * @return true if the name is valid, false otherwise.
     */
    private boolean isValidName(String name, String type) {
        if (name == null || name.trim().isEmpty()) {
            logger.error("Invalid {} name: Name is null or empty", type);
            return false;
        }

        if (!name.matches(NAME_REGEX)) {
            logger.error("Invalid {} name format: Name does not match the required pattern", type);
            return false;
        }

        if (type.equals("first") && !Character.isUpperCase(name.charAt(0))) {
            logger.error("First name should start with an uppercase letter: {}", name);
            return false;
        }

        if (type.equals("last") && !name.equals(name.toUpperCase())) {
            logger.error("Last name should be all uppercase letters: {}", name);
            return false;
        }

        logger.info("Valid {} name: {}", type, name);
        return true;
    }
}