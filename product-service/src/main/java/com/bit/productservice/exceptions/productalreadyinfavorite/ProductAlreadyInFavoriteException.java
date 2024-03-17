package com.bit.productservice.exceptions.productalreadyinfavorite;

public class ProductAlreadyInFavoriteException extends RuntimeException {
    public ProductAlreadyInFavoriteException(String message) {
        super(message);
    }
}
