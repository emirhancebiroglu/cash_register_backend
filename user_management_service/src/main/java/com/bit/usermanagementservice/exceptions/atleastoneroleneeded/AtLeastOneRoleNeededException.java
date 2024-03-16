package com.bit.usermanagementservice.exceptions.atleastoneroleneeded;

public class AtLeastOneRoleNeededException extends IllegalArgumentException {
    public AtLeastOneRoleNeededException(String message) {
        super(message);
    }
}