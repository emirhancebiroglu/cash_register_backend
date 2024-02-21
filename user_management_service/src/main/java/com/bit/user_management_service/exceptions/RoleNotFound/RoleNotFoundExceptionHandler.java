package com.bit.user_management_service.exceptions.RoleNotFound;

import com.bit.user_management_service.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RoleNotFoundExceptionHandler {
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<?> roleNotFoundException(RoleNotFoundException rnf){
        ErrorDetails errorDetails = ErrorDetails
            .builder()
            .errorMessage(rnf.getMessage())
            .status(HttpStatus.NOT_FOUND.name())
            .statusCode(HttpStatus.NOT_FOUND.value())
            .timeStamp(LocalDateTime.now())
            .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
