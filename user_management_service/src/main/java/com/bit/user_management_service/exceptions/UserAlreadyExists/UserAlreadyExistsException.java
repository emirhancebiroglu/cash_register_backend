package com.bit.user_management_service.exceptions.UserAlreadyExists;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
