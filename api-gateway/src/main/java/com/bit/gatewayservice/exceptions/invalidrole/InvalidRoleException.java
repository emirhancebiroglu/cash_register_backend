package com.bit.gatewayservice.exceptions.invalidrole;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message) {
        super(message);
    }
}