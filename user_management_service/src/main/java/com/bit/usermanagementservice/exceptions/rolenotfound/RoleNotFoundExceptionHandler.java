package com.bit.usermanagementservice.exceptions.rolenotfound;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controller advice to handle RoleNotFoundExceptions globally.
 */
@ControllerAdvice
public class RoleNotFoundExceptionHandler {
    /**
     * Handles RoleNotFoundException and returns an appropriate ResponseEntity.
     * @param ex The RoleNotFoundException instance.
     * @return ResponseEntity containing error details and HTTP status.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorDetails> roleNotFoundException(RoleNotFoundException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
