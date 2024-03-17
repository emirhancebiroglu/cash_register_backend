package com.bit.gatewayservice.exceptions.missingauthorizationheader;

import com.bit.gatewayservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class MissingAuthorizationHeaderExceptionHandler {
    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(MissingAuthorizationHeaderException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
}
