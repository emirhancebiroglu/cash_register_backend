package com.bit.usermanagementservice.exceptions.useralreadyexists;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * The UserAlreadyExistsExceptionHandler class provides exception handling for UserAlreadyExistsException.
 * It is responsible for generating appropriate error responses when UserAlreadyExistsException occurs.
 */
@ControllerAdvice
public class UserAlreadyExistsExceptionHandler {
    /**
     * Handles UserAlreadyExistsException and generates an appropriate error response.
     *
     * @param ex the UserAlreadyExistsException to be handled.
     * @return a ResponseEntity containing the error details and HTTP status code.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> userAlreadyExistsException(UserAlreadyExistsException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
