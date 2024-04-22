package com.bit.productservice.exceptions.productwithsamename;

/**
 * Exception thrown when a product with the same name is encountered.
 */
public class ProductWithSameNameException extends RuntimeException {
    /**
     * Constructs a new ProductWithSameNameException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductWithSameNameException(String message) {
        super(message);
    }
}
