package com.bit.productservice.exceptions.nocodeprovided;

/**
 * Exception thrown when no code is provided.
 */
public class NoCodeProvidedException extends IllegalArgumentException {
    /**
     * Constructs a new NoCodeProvidedException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NoCodeProvidedException(String message) {
        super(message);
    }
}
