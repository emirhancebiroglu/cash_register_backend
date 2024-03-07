package com.bit.usermanagementservice.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailValidator {
    private static final Logger logger = LoggerFactory.getLogger(EmailValidator.class);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        boolean isValid = matcher.matches();

        if (isValid){
            logger.info("Email '{}' is valid", email);
        }
        else{
            logger.error("Email '{}' is not valid", email);
        }

        return isValid;
    }
}
