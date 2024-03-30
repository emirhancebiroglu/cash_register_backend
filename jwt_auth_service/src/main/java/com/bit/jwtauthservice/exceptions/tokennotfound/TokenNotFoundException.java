package com.bit.jwtauthservice.exceptions.tokennotfound;

import org.springframework.dao.DataAccessException;

public class TokenNotFoundException extends DataAccessException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
