package com.bit.jwtauthservice.exceptions.resettokenexpiration;

import com.bit.jwtauthservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class InvalidResetTokenExceptionHandler {
    /**
     * Handles InvalidResetTokenException and returns an error response with appropriate details.
     * @param ex the InvalidResetTokenException
     * @return ResponseEntity containing the error details
     */
    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<ErrorDetails> invalidResetTokenExceptionHandler(InvalidResetTokenException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
