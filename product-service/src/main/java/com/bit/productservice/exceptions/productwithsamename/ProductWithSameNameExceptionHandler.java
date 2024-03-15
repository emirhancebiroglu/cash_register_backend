package com.bit.productservice.exceptions.productwithsamename;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ProductWithSameNameExceptionHandler {
    @ExceptionHandler(ProductWithSameNameException.class)
    public ResponseEntity<ErrorDetails> handleProductWithSameNameException(ProductWithSameNameException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.CONFLICT.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
