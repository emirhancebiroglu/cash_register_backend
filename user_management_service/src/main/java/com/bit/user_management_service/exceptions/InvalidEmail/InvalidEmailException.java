package com.bit.user_management_service.exceptions.InvalidEmail;

public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(String message) {
        super(message);
    }
}