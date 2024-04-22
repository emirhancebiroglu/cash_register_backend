package com.bit.productservice.exceptions.productwithsamebarcode;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for ProductWithSameBarcodeException.
 */
@ControllerAdvice
public class ProductWithSameBarcodeExceptionHandler {
    /**
     * Handles ProductWithSameBarcodeException and returns an appropriate ResponseEntity.
     *
     * @param ex The ProductWithSameBarcodeException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(ProductWithSameBarcodeException.class)
    public ResponseEntity<ErrorDetails> handleProductWithSameBarcodeException(ProductWithSameBarcodeException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
