package com.bit.user_management_service.exceptions.UserAlreadyActive;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}