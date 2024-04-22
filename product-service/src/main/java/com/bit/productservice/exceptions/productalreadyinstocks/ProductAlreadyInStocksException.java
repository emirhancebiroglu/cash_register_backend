package com.bit.productservice.exceptions.productalreadyinstocks;

/**
 * Exception thrown when the product is already in stocks.
 */
public class ProductAlreadyInStocksException extends RuntimeException {
    /**
     * Constructs a new ProductAlreadyInStocksException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductAlreadyInStocksException(String message) {
        super(message);
    }
}
