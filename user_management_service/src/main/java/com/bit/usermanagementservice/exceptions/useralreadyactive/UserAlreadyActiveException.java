package com.bit.usermanagementservice.exceptions.useralreadyactive;

/**
 * Exception indicating that a user is already active.
 */
public class UserAlreadyActiveException extends RuntimeException {
    /**
     * Constructs a new UserAlreadyActiveException with the specified detail message.
     * @param message the detail message.
     */
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}