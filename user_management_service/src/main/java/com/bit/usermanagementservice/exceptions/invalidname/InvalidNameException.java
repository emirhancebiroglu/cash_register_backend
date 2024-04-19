package com.bit.usermanagementservice.exceptions.invalidname;

/**
 * The InvalidNameException is thrown to indicate that a given name is invalid.
 * This exception typically occurs when a name fails validation checks.
 */
public class InvalidNameException extends IllegalArgumentException {
    /**
     * Constructs a new InvalidNameException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public InvalidNameException(String message) {
        super(message);
    }
}