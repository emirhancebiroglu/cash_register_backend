package com.bit.user_management_service.exceptions.UserNotFound;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
