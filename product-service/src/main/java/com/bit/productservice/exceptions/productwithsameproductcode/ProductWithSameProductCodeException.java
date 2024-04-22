package com.bit.productservice.exceptions.productwithsameproductcode;

/**
 * Exception thrown when a product with the same product code is encountered.
 */
public class ProductWithSameProductCodeException extends RuntimeException {
    /**
     * Constructs a new ProductWithSameProductCodeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductWithSameProductCodeException(String message) {
        super(message);
    }
}
