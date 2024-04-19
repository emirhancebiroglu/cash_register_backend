package com.bit.usermanagementservice.exceptions.invalidname;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * The InvalidNameExceptionHandler class provides exception handling for InvalidNameException.
 * It is responsible for generating appropriate error responses when InvalidNameException occurs.
 */
@ControllerAdvice
public class InvalidNameExceptionHandler {
    /**
     * Handles InvalidNameException and generates an appropriate error response.
     *
     * @param ex the InvalidNameException to be handled.
     * @return a ResponseEntity containing the error details and HTTP status code.
     */
    @ExceptionHandler(InvalidNameException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(InvalidNameException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
