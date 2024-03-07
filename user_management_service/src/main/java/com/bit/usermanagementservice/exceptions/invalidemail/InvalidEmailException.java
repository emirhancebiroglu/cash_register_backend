package com.bit.usermanagementservice.exceptions.invalidemail;

public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(String message) {
        super(message);
    }
}