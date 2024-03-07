package com.bit.usermanagementservice.exceptions.useralreadyactive;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}