package com.bit.productservice.exceptions.productalreadyinfavorite;

/**
 * Exception thrown when the product is already in favorites.
 */
public class ProductAlreadyInFavoriteException extends RuntimeException {
    /**
     * Constructs a new ProductAlreadyInFavoriteException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductAlreadyInFavoriteException(String message) {
        super(message);
    }
}
