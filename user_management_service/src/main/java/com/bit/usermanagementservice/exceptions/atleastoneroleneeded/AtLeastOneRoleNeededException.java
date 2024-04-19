package com.bit.usermanagementservice.exceptions.atleastoneroleneeded;

/**
 * Thrown to indicate that at least one role is needed.
 */
public class AtLeastOneRoleNeededException extends IllegalArgumentException {
    /**
     * Constructs a new AtLeastOneRoleNeededException with the specified detail message.
     *
     * @param message the detail message.
     */
    public AtLeastOneRoleNeededException(String message) {
        super(message);
    }
}