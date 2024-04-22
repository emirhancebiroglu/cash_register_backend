package com.bit.productservice.exceptions.productalreadydeleted;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for ProductAlreadyDeletedException.
 */
@ControllerAdvice
public class ProductAlreadyDeletedExceptionHandler {
    /**
     * Handles ProductAlreadyDeletedException and returns an appropriate ResponseEntity.
     *
     * @param ex The ProductAlreadyDeletedException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(ProductAlreadyDeletedException.class)
    public ResponseEntity<ErrorDetails> handleProductAlreadyDeletedException(ProductAlreadyDeletedException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
