package com.bit.jwtauthservice.exceptions.expiredrefreshtoken;

/**
 * Exception thrown when a refresh token has expired.
 */
public class ExpiredRefreshTokenException extends RuntimeException {
    /**
     * Constructs a new ExpiredRefreshTokenException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ExpiredRefreshTokenException(String message) {
        super(message);
    }
}
