package com.bit.productservice.exceptions.nulloremptyfield;

/**
 * Exception thrown when a field is null or empty.
 */
public class NullOrEmptyFieldException extends RuntimeException {
    /**
     * Constructs a new NullOrEmptyFieldException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NullOrEmptyFieldException(String message) {
        super(message);
    }
}
