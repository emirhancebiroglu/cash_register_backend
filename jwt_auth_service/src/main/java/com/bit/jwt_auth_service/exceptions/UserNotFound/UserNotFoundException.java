package com.bit.jwt_auth_service.exceptions.UserNotFound;

import org.springframework.dao.DataAccessException;

public class UserNotFoundException extends DataAccessException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
