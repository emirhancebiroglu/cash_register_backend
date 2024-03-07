package com.bit.usermanagementservice.exceptions.useralreadydeleted;

public class UserAlreadyDeletedException extends RuntimeException {
    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}