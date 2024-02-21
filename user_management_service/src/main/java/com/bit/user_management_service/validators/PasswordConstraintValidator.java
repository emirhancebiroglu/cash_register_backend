package com.bit.user_management_service.validators;

import com.bit.user_management_service.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.passay.*;

import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

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
