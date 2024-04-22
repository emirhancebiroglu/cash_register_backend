package com.bit.productservice.exceptions.productwithsamebarcode;

/**
 * Exception thrown when a product with the same barcode is encountered.
 */
public class ProductWithSameBarcodeException extends RuntimeException {
    /**
     * Constructs a new ProductWithSameBarcodeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ProductWithSameBarcodeException(String message) {
        super(message);
    }
}
