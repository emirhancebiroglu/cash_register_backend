package com.bit.jwtauthservice.exceptions.incorrectoldpassword;

public class IncorrectOldPasswordException extends RuntimeException {
    public IncorrectOldPasswordException(String message) {
        super(message);
    }
}
