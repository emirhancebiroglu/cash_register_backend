package com.bit.productservice.exceptions.productwithsamebarcode;

public class ProductWithSameBarcodeException extends RuntimeException {
    public ProductWithSameBarcodeException(String message) {
        super(message);
    }
}
