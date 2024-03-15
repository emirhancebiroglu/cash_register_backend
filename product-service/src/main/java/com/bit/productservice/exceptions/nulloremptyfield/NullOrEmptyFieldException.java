package com.bit.productservice.exceptions.nulloremptyfield;

public class NullOrEmptyFieldException extends RuntimeException {
    public NullOrEmptyFieldException(String message) {
        super(message);
    }
}
