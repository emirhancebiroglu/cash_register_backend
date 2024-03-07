package com.bit.jwtauthservice.exceptions.badcredentials;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
