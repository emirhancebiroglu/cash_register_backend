package com.bit.user_management_service.exceptions.InvalidName;

public class InvalidNameException extends IllegalArgumentException {
    public InvalidNameException(String message) {
        super(message);
    }
}