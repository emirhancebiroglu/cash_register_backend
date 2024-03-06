package com.bit.usermanagementservice.exceptions.UserAlreadyActive;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}