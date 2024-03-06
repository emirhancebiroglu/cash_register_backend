package com.bit.usermanagementservice.exceptions.RoleNotFound;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RoleNotFoundExceptionHandler {
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorDetails> roleNotFoundException(RoleNotFoundException ex){
        ErrorDetails errorDetails = ErrorDetails
            .builder()
            .errorMessage(ex.getMessage())
            .status(HttpStatus.NOT_FOUND.name())
            .statusCode(HttpStatus.NOT_FOUND.value())
            .timeStamp(LocalDateTime.now())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
