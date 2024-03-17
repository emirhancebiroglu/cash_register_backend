package com.bit.gatewayservice.exceptions.missingauthorizationheader;

public class MissingAuthorizationHeaderException extends RuntimeException {
    public MissingAuthorizationHeaderException(String message) {
        super(message);
    }
}