package com.bit.jwtauthservice.exceptions.samepassword;

import com.bit.jwtauthservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class SamePasswordExceptionHandler {
    /**
     * Handles SamePasswordException and returns an HTTP response with the appropriate error details.
     *
     * @param ex the SamePasswordException
     * @return ResponseEntity containing the error details
     */
    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ErrorDetails> samePasswordException(SamePasswordException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
