package com.bit.productservice.exceptions.nocodeprovided;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for NoCodeProvidedException.
 */
@ControllerAdvice
public class NoCodeProvidedExceptionHandler {
    /**
     * Handles NoCodeProvidedException and returns an appropriate ResponseEntity.
     *
     * @param ex The NoCodeProvidedException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(NoCodeProvidedException.class)
    public ResponseEntity<ErrorDetails> handleNoCodeProvidedException(NoCodeProvidedException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
