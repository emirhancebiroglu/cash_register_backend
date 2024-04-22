package com.bit.productservice.exceptions.productwithsamename;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for ProductWithSameNameException.
 */
@ControllerAdvice
public class ProductWithSameNameExceptionHandler {
    /**
     * Handles ProductWithSameNameException and returns an appropriate ResponseEntity.
     *
     * @param ex The ProductWithSameNameException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(ProductWithSameNameException.class)
    public ResponseEntity<ErrorDetails> handleProductWithSameNameException(ProductWithSameNameException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
