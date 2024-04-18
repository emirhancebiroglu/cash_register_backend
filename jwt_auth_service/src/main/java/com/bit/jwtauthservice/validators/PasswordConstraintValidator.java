package com.bit.jwtauthservice.validators;

import com.bit.jwtauthservice.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.passay.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Validator for password constraints based on the Passay library.
 */
@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    /**
     * Initializes the validator.
     *
     * @param constraintAnnotation The annotation to initialize.
     */
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Validates the password against specified constraints.
     *
     * @param password The password to validate.
     * @param context  The context in which the constraint is evaluated.
     * @return true if the password is valid, false otherwise.
     */
    @SneakyThrows
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator passwordValidator = new PasswordValidator(
            Arrays.asList(
            new LengthRule(8, 32),

            new CharacterRule(EnglishCharacterData.UpperCase, 1),

            new CharacterRule(EnglishCharacterData.LowerCase, 1),

            new CharacterRule(EnglishCharacterData.Digit, 1),

            new WhitespaceRule()
        )
        );

        RuleResult result = passwordValidator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        boolean isMessageExist = passwordValidator.getMessages(result).stream().findFirst().isPresent();

        if (isMessageExist) {
            context.buildConstraintViolationWithTemplate(passwordValidator.getMessages(result).stream().findFirst().get())
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }
        return false;
    }
}
