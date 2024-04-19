package com.bit.usermanagementservice.exceptions.usernotfound;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * The UserNotFoundExceptionHandler class provides exception handling for UserNotFoundException.
 * It is responsible for generating appropriate error responses when UserNotFoundException occurs.
 */
@ControllerAdvice
public class UserNotFoundExceptionHandler {
    /**
     * Handles UserNotFoundException and generates an appropriate error response.
     *
     * @param ex the UserNotFoundException to be handled.
     * @return a ResponseEntity containing the error details and HTTP status code.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> userNotFoundException(UserNotFoundException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
