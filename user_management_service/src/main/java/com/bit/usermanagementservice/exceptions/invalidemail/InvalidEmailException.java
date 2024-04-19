package com.bit.usermanagementservice.exceptions.invalidemail;

/**
 * Exception thrown when an invalid email is encountered.
 */
public class InvalidEmailException extends IllegalArgumentException {
    /**
     * Constructs an InvalidEmailException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}