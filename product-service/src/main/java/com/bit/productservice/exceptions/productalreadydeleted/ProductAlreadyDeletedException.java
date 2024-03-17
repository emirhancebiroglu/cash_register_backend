package com.bit.productservice.exceptions.productalreadydeleted;

public class ProductAlreadyDeletedException extends RuntimeException {
    public ProductAlreadyDeletedException(String message) {
        super(message);
    }
}
