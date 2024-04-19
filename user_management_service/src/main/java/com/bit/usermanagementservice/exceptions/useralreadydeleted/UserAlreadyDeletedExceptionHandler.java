package com.bit.usermanagementservice.exceptions.useralreadydeleted;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controller advice to handle UserAlreadyDeletedException.
 */
@ControllerAdvice
public class UserAlreadyDeletedExceptionHandler {
    /**
     * Handles UserAlreadyDeletedException and returns an appropriate response entity.
     * @param ex The exception to handle.
     * @return ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(UserAlreadyDeletedException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(UserAlreadyDeletedException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
