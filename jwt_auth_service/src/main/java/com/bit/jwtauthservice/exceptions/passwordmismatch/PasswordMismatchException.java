package com.bit.jwtauthservice.exceptions.passwordmismatch;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
