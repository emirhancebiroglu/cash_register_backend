package com.bit.jwt_auth_service.exceptions.InvalidName;

public class InvalidNameException extends IllegalArgumentException {
    public InvalidNameException(String message) {
        super(message);
    }
}