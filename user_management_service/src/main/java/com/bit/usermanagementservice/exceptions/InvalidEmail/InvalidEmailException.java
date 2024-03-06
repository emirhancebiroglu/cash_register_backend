package com.bit.usermanagementservice.exceptions.InvalidEmail;

public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(String message) {
        super(message);
    }
}