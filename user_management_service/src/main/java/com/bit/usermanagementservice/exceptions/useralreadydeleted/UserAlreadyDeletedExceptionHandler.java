package com.bit.usermanagementservice.exceptions.useralreadydeleted;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class UserAlreadyDeletedExceptionHandler {
    @ExceptionHandler(UserAlreadyDeletedException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(UserAlreadyDeletedException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        errorDetails.setErrorMessage(ex.getMessage());
        errorDetails.setStatus(HttpStatus.CONFLICT.name());
        errorDetails.setStatusCode(HttpStatus.CONFLICT.value());
        errorDetails.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }


}
