package com.bit.jwt_auth_service.exceptions.InvalidEmail;

public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(String message) {
        super(message);
    }
}