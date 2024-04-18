package com.bit.jwtauthservice.exceptions.tokennotfound;

import org.springframework.dao.DataAccessException;

public class TokenNotFoundException extends DataAccessException {
    /**
     * Constructs a TokenNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public TokenNotFoundException(String message) {
        super(message);
    }
}
