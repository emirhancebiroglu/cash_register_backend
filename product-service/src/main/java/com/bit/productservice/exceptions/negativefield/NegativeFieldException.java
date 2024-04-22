package com.bit.productservice.exceptions.negativefield;

/**
 * Exception thrown when a negative field is encountered.
 */
public class NegativeFieldException extends RuntimeException {
    /**
     * Constructs a new NegativeFieldException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NegativeFieldException(String message) {
        super(message);
    }
}
