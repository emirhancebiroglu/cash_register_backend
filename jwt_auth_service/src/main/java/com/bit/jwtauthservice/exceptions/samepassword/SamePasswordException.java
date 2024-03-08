package com.bit.jwtauthservice.exceptions.samepassword;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException(String message) {
        super(message);
    }
}
