package com.bit.usermanagementservice.exceptions.UserAlreadyExists;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
