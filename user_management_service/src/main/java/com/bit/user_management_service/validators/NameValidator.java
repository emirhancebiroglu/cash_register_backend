package com.bit.user_management_service.validators;

import org.springframework.stereotype.Component;

@Component
public class NameValidator {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 18;
    private static final String NAME_REGEX = "^[a-zA-Z]{3,18}$";

    public boolean validateFirstName(String firstName) {
        return isValidFirstName(firstName);
    }

    public boolean validateLastName(String lastName) {
        return isValidLastName(lastName);
    }

    private boolean isValidFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }

        if (!firstName.matches(NAME_REGEX) || firstName.trim().length() < MIN_LENGTH || firstName.trim().length() > MAX_LENGTH) {
            return false;
        }

        if (!Character.isUpperCase(firstName.charAt(0))) {
            return false;
        }
        for (int i = 1; i < firstName.length(); i++) {
            if (!Character.isLowerCase(firstName.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            return false;
        }

        if (!lastName.matches(NAME_REGEX) || lastName.trim().length() < MIN_LENGTH || lastName.trim().length() > MAX_LENGTH) {
            return false;
        }

        return lastName.equals(lastName.toUpperCase());
    }
}
