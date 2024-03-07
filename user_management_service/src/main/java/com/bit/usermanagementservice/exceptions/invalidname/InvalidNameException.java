package com.bit.usermanagementservice.exceptions.invalidname;

public class InvalidNameException extends IllegalArgumentException {
    public InvalidNameException(String message) {
        super(message);
    }
}