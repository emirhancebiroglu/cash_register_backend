package com.bit.usermanagementservice.exceptions.useralreadyexists;

/**
 * The UserAlreadyExistsException is thrown to indicate that a user already exists.
 * This exception typically occurs when trying to create a user with a username or email that already exists in the system.
 */
public class UserAlreadyExistsException extends RuntimeException{
    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
