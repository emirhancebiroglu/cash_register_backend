package com.bit.productservice.exceptions.productwithsamename;

public class ProductWithSameNameException extends RuntimeException {
    public ProductWithSameNameException(String message) {
        super(message);
    }
}
