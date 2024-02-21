package com.bit.user_management_service.exceptions.RoleNotFound;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message) {
        super(message);
    }
}
