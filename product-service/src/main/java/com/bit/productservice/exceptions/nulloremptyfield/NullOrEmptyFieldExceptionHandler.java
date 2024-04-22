package com.bit.productservice.exceptions.nulloremptyfield;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for NullOrEmptyFieldException.
 */
@ControllerAdvice
public class NullOrEmptyFieldExceptionHandler {
    /**
     * Handles NullOrEmptyFieldException and returns an appropriate ResponseEntity.
     *
     * @param ex The NullOrEmptyFieldException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(NullOrEmptyFieldException.class)
    public ResponseEntity<ErrorDetails> handleNullOrEmptyFieldException(NullOrEmptyFieldException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
