package com.bit.usermanagementservice.exceptions.usernotfound;

import org.springframework.dao.DataAccessException;

/**
 * The UserNotFoundException is thrown to indicate that a user could not be found.
 * This exception typically occurs when trying to access or manipulate a user that does not exist in the system.
 */
public class UserNotFoundException extends DataAccessException {
    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
