package com.bit.user_management_service.exceptions.UserAlreadyDeleted;

import com.bit.user_management_service.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class UserAlreadyDeletedExceptionHandler {
    @ExceptionHandler(UserAlreadyDeletedException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(UserAlreadyDeletedException ex) {
        ErrorDetails errorDetails = ErrorDetails
              .builder()
              .errorMessage(ex.getMessage())
              .status(HttpStatus.BAD_REQUEST.name())
              .timeStamp(LocalDateTime.now())
              .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


}
