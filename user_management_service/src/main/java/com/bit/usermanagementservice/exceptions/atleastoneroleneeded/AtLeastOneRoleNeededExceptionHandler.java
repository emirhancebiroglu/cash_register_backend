package com.bit.usermanagementservice.exceptions.atleastoneroleneeded;

import com.bit.usermanagementservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AtLeastOneRoleNeededExceptionHandler {
    @ExceptionHandler(AtLeastOneRoleNeededException.class)
    public ResponseEntity<ErrorDetails> handleInvalidEmailException(AtLeastOneRoleNeededException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
