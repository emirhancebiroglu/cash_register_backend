package com.bit.jwtauthservice.exceptions.invalidrefreshtoken;

public class InvalidRefreshTokenException extends IllegalArgumentException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}