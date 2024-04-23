package bit.salesservice.exceptions.uncompletedcheckoutexception;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for UncompletedCheckoutException.
 */
@ControllerAdvice
public class UncompletedCheckoutExceptionHandler {
    /**
     * Handles UncompletedCheckoutException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex The UncompletedCheckoutException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(UncompletedCheckoutException.class)
    public ResponseEntity<ErrorDetails> handleUncompletedCheckoutExceptionHandler(UncompletedCheckoutException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
