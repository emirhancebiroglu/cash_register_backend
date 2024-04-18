package com.bit.jwtauthservice.exceptions.usernotfound;

import org.springframework.dao.DataAccessException;

public class UserNotFoundException extends DataAccessException {
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
