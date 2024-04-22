package com.bit.productservice.exceptions.bothcodetypeprovided;

/**
 * Exception thrown when both code types are provided.
 */
public class BothCodeTypeProvidedException extends IllegalArgumentException {
    /**
     * Constructs a new BothCodeTypeProvidedException with the specified detail message.
     *
     * @param message the detail message.
     */
    public BothCodeTypeProvidedException(String message) {
        super(message);
    }
}
