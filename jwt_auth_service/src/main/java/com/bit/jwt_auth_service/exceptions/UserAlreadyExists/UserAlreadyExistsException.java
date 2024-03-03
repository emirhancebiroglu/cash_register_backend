package com.bit.jwt_auth_service.exceptions.UserAlreadyExists;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
