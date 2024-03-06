package com.bit.usermanagementservice.exceptions.UserAlreadyExists;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class UserAlreadyExistsExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> userAlreadyExistsException(UserAlreadyExistsException ex){
        ErrorDetails errorDetails = ErrorDetails
            .builder()
            .errorMessage(ex.getMessage())
            .status(HttpStatus.CONFLICT.name())
            .statusCode(HttpStatus.CONFLICT.value())
            .timeStamp(LocalDateTime.now())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
