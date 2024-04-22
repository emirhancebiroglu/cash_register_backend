package com.bit.productservice.exceptions.productalreadydeleted;

/**
 * Exception thrown when the product is already deleted.
 */
public class ProductAlreadyDeletedException extends RuntimeException {
    /**
     * Constructs a new ProductAlreadyDeletedException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductAlreadyDeletedException(String message) {
        super(message);
    }
}
