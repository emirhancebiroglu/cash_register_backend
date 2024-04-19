package com.bit.usermanagementservice.exceptions.useralreadyactive;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controller advice to handle UserAlreadyActiveException.
 */
@ControllerAdvice
public class UserAlreadyActiveExceptionHandler {
    /**
     * Handles UserAlreadyActiveException and returns an appropriate response entity.
     * @param ex The exception to handle.
     * @return ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(UserAlreadyActiveException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(UserAlreadyActiveException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }


}
