package com.bit.jwtauthservice.exceptions.badcredentials;

import com.bit.jwtauthservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controller advice to handle BadCredentialsException and provide appropriate error response.
 */
@ControllerAdvice
public class BadCredentialsExceptionHandler {
    /**
     * Handles BadCredentialsException and returns a ResponseEntity with an error message and status code.
     * @param ex the BadCredentialsException
     * @return ResponseEntity containing error details
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> badCredentialsExceptionHandler(BadCredentialsException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
}
