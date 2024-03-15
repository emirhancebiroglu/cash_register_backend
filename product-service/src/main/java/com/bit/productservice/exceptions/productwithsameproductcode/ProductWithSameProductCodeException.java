package com.bit.productservice.exceptions.productwithsameproductcode;

public class ProductWithSameProductCodeException extends RuntimeException {
    public ProductWithSameProductCodeException(String message) {
        super(message);
    }
}
