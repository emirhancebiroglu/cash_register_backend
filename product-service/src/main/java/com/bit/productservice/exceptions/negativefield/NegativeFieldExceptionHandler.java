package com.bit.productservice.exceptions.negativefield;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class NegativeFieldExceptionHandler {
    @ExceptionHandler(NegativeFieldException.class)
    public ResponseEntity<ErrorDetails> handleNegativeFieldException(NegativeFieldException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_GATEWAY.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_GATEWAY);
    }
}
