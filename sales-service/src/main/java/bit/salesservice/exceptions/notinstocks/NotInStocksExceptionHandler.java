package bit.salesservice.exceptions.notinstocks;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for NotInStocksException.
 */
@ControllerAdvice
public class NotInStocksExceptionHandler {
    /**
     * Handles NotInStocksException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex The NotInStocksException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(NotInStocksException.class)
    public ResponseEntity<ErrorDetails> handleNotInStocksExceptionHandler(NotInStocksException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
