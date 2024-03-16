package com.bit.usermanagementservice.validators;

import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.exceptions.atleastoneroleneeded.AtLeastOneRoleNeededException;
import com.bit.usermanagementservice.exceptions.invalidemail.InvalidEmailException;
import com.bit.usermanagementservice.exceptions.invalidname.InvalidNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final EmailValidator emailValidator;
    private final NameValidator nameValidator;

    public void validateUserData(AddUserReq addUserReq){
        validateEmail(addUserReq.getEmail());
        validateFirstName(addUserReq.getFirstName());
        validateLastName(addUserReq.getLastName());
        validateRoles(addUserReq.getRoles());
    }

    public void validateEmail(String email) {
        if (!emailValidator.isValidEmail(email)){
            throw new InvalidEmailException("Invalid email: " + email);
        }
    }

    public void validateFirstName(String firstName){
        if (!nameValidator.validateFirstName(firstName)){
            throw new InvalidNameException("Invalid first name: " + firstName);
        }
    }

    public void validateLastName(String lastName){
        if (!nameValidator.validateLastName(lastName)){
            throw new InvalidNameException("Invalid last name: " + lastName);
        }
    }

    public void validateRoles(Set<String> roleNames){
        if (roleNames  == null || roleNames .isEmpty()){
            throw new AtLeastOneRoleNeededException("At least one role must be specified");
        }
    }
}
