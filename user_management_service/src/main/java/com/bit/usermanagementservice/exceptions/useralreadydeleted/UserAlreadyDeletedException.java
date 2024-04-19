package com.bit.usermanagementservice.exceptions.useralreadydeleted;

/**
 * This exception is thrown when attempting to delete a user that has already been deleted.
 */
public class UserAlreadyDeletedException extends RuntimeException {
    /**
     * Constructs a new UserAlreadyDeletedException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}