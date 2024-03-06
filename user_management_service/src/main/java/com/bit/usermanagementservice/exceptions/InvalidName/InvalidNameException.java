package com.bit.usermanagementservice.exceptions.InvalidName;

public class InvalidNameException extends IllegalArgumentException {
    public InvalidNameException(String message) {
        super(message);
    }
}