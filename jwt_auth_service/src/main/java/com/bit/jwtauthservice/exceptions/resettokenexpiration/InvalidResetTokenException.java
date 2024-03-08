package com.bit.jwtauthservice.exceptions.resettokenexpiration;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException(String message) {
        super(message);
    }
}
