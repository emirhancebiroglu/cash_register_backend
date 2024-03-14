package com.bit.productservice.exceptions.productisnotfavorite;

public class ProductIsNotFavoriteException extends RuntimeException {
    public ProductIsNotFavoriteException(String message) {
        super(message);
    }
}
