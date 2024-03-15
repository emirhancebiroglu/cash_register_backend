package com.bit.productservice.exceptions.nocodeprovided;

public class NoCodeProvidedException extends IllegalArgumentException {
    public NoCodeProvidedException(String message) {
        super(message);
    }
}
