package com.bit.jwt_auth_service.exceptions.UserAlreadyActive;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}