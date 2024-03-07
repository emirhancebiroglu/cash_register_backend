package com.bit.usermanagementservice.exceptions.useralreadyexists;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
