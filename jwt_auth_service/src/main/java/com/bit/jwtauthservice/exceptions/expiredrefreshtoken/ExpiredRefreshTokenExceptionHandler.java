package com.bit.jwtauthservice.exceptions.expiredrefreshtoken;

import com.bit.jwtauthservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for handling ExpiredRefreshTokenException.
 */
@ControllerAdvice
public class ExpiredRefreshTokenExceptionHandler {
    /**
     * Handles ExpiredRefreshTokenException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex the ExpiredRefreshTokenException to handle.
     * @return a ResponseEntity containing error details.
     */
    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ErrorDetails> expiredRefreshTokenExceptionHandler(ExpiredRefreshTokenException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
