package com.bit.usermanagementservice.exceptions.UserAlreadyDeleted;

public class UserAlreadyDeletedException extends RuntimeException {
    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}