package com.bit.user_management_service.exceptions.UserAlreadyDeleted;

public class UserAlreadyDeletedException extends RuntimeException {
    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}