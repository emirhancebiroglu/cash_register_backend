package com.bit.productservice.exceptions.productalreadyinstocks;

public class ProductAlreadyInStocksException extends RuntimeException {
    public ProductAlreadyInStocksException(String message) {
        super(message);
    }
}
