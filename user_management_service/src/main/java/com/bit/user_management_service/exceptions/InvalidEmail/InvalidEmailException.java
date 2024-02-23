package com.bit.user_management_service.exceptions.InvalidEmail;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}