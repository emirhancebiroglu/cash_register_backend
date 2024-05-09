package com.bit.usermanagementservice.validators;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.exceptions.atleastoneroleneeded.AtLeastOneRoleNeededException;
import com.bit.usermanagementservice.exceptions.invalidemail.InvalidEmailException;
import com.bit.usermanagementservice.exceptions.invalidname.InvalidNameException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The UserValidator class is responsible for validating user data.
 * It validates user email, first name, last name, and role information.
 * It uses EmailValidator and NameValidator for email and name validation, respectively.
 */
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final EmailValidator emailValidator;
    private final NameValidator nameValidator;

    private static final Logger logger = LogManager.getLogger(UserValidator.class);


    /**
     * Validates user data including email, first name, last name, and roles.
     *
     * @param addUserReq the request containing user data to validate.
     * @throws InvalidEmailException if the email is invalid.
     * @throws InvalidNameException if the first name or last name is invalid.
     * @throws AtLeastOneRoleNeededException if no roles are specified.
     */
    public void validateUserData(AddUserReq addUserReq){
        validateEmail(addUserReq.getEmail());
        validateFirstName(addUserReq.getFirstName());
        validateLastName(addUserReq.getLastName());
        validateRoles(addUserReq.getRoles());
    }

    /**
     * Validates an email address.
     *
     * @param email the email address to validate.
     * @throws InvalidEmailException if the email is invalid.
     */
    public void validateEmail(String email) {
        if (!emailValidator.isValidEmail(email)){
            logger.error("Invalid email address : {}", email);
            throw new InvalidEmailException("Invalid email: " + email);
        }
    }

    /**
     * Validates a first name.
     *
     * @param firstName the first name to validate.
     * @throws InvalidNameException if the first name is invalid.
     */
    public void validateFirstName(String firstName){
        if (!nameValidator.validateFirstName(firstName)){
            logger.error("Invalid first name : {}", firstName);
            throw new InvalidNameException("Invalid first name: " + firstName);
        }
    }

    /**
     * Validates a last name.
     *
     * @param lastName the last name to validate.
     * @throws InvalidNameException if the last name is invalid.
     */
    public void validateLastName(String lastName){
        if (!nameValidator.validateLastName(lastName)){
            logger.error("Invalid last name : {}", lastName);
            throw new InvalidNameException("Invalid last name: " + lastName);
        }
    }

    /**
     * Validates user roles.
     *
     * @param roleNames the set of role names to validate.
     * @throws AtLeastOneRoleNeededException if no roles are specified.
     */
    public void validateRoles(Set<String> roleNames){
        if (roleNames  == null || roleNames .isEmpty()){
            logger.error("At least one role must be specified");
            throw new AtLeastOneRoleNeededException("At least one role must be specified");
        }
    }
}
