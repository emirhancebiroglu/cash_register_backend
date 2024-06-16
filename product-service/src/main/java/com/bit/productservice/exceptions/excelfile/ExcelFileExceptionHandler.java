package com.bit.productservice.exceptions.excelfile;

import com.bit.productservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for ExcelFileException.
 */
@ControllerAdvice
public class ExcelFileExceptionHandler {
    /**
     * Handles ExcelFileException and returns an appropriate ResponseEntity.
     *
     * @param ex The ExcelFileException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(ExcelFileException.class)
    public ResponseEntity<ErrorDetails> handleExcelFileException(ExcelFileException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
