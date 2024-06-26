package com.bit.usermanagementservice.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The EmailValidator class is responsible for validating email addresses.
 * It checks whether an email address matches the standard email format using a regular expression.
 * It logs validation results using SLF4J logger.
 */
@Component
public class EmailValidator {
    private static final Logger logger = LogManager.getLogger(EmailValidator.class);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    /**
     * Validates an email address.
     *
     * @param email the email address to validate.
     * @return true if the email address is valid, false otherwise.
     */
    public boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        boolean isValid = matcher.matches();

        if (isValid){
            logger.trace("Email '{}' is valid", email);
        }
        else{
            logger.trace("Email '{}' is not valid", email);
        }

        return isValid;
    }
}
