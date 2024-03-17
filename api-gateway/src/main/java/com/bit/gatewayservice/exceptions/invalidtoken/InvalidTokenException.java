package com.bit.gatewayservice.exceptions.invalidtoken;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}