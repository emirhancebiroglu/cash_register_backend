package com.bit.jwtauthservice.exceptions.badcredentials;

/**
 * Exception indicating that the provided credentials are invalid.
 */
public class BadCredentialsException extends RuntimeException {
    /**
     * Constructs a new BadCredentialsException with the specified detail message.
     * @param message the detail message
     */
    public BadCredentialsException(String message) {
        super(message);
    }
}
