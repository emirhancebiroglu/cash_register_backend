package com.bit.productservice.exceptions.invalidsearchtype;

/**
 * Exception thrown when provided search type is invalid.
 */
public class InvalidSearchTypeException extends IllegalArgumentException {
    /**
     * Constructs a new InvalidSearchTypeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidSearchTypeException(String message) {
        super(message);
    }
}
