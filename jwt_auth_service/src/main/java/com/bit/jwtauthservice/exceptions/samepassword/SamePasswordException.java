package com.bit.jwtauthservice.exceptions.samepassword;

/**
 * Exception indicating that the new password provided is the same as the old password.
 */
public class SamePasswordException extends RuntimeException {
    /**
     * Constructs a new SamePasswordException with the specified detail message.
     *
     * @param message the detail message
     */
    public SamePasswordException(String message) {
        super(message);
    }
}
