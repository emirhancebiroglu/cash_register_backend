package com.bit.jwtauthservice.exceptions.resettokenexpiration;

/**
 * Exception indicating that the reset token is invalid.
 */
public class InvalidResetTokenException extends RuntimeException {
    /**
     * Constructs a new InvalidResetTokenException with the specified detail message.
     * @param message the detail message
     */
    public InvalidResetTokenException(String message) {
        super(message);
    }
}
