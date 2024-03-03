package com.bit.jwt_auth_service.exceptions.UserAlreadyDeleted;

public class UserAlreadyDeletedException extends RuntimeException {
    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}