package com.bit.usermanagementservice.exceptions.invalidstatustype;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for InvalidStatusTypeException.
 */
@ControllerAdvice
public class InvalidStatusTypeExceptionHandler {
    /**
     * Handles InvalidStatusTypeException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex The InvalidStatusTypeException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(InvalidStatusTypeException.class)
    public ResponseEntity<ErrorDetails> handleInvalidStatusTypeExceptionHandler(InvalidStatusTypeException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
