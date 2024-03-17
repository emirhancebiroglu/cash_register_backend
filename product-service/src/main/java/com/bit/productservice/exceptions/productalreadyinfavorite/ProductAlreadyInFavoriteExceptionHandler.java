package com.bit.productservice.exceptions.productalreadyinfavorite;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ProductAlreadyInFavoriteExceptionHandler {
    @ExceptionHandler(ProductAlreadyInFavoriteException.class)
    public ResponseEntity<ErrorDetails> handleProductAlreadyInFavoriteException(ProductAlreadyInFavoriteException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
