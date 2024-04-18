package com.bit.jwtauthservice.exceptions.passwordmismatch;

import com.bit.jwtauthservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controller advice to handle PasswordMismatchException and provide appropriate error response.
 */
@ControllerAdvice
public class PasswordMismatchExceptionHandler {
    /**
     * Handles PasswordMismatchException and returns a ResponseEntity with an error message and status code.
     * @param ex the PasswordMismatchException
     * @return ResponseEntity containing error details
     */
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorDetails> passwordMismatchExceptionHandler(PasswordMismatchException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
