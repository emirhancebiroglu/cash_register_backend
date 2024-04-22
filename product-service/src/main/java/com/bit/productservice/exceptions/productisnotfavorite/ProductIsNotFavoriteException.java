package com.bit.productservice.exceptions.productisnotfavorite;

/**
 * Exception thrown when the product is not marked as a favorite.
 */
public class ProductIsNotFavoriteException extends RuntimeException {
    /**
     * Constructs a new ProductIsNotFavoriteException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductIsNotFavoriteException(String message) {
        super(message);
    }
}
