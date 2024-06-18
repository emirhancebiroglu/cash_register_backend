package bit.salesservice.exceptions.unavailableservice;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for UnavailableServiceException.
 */
@ControllerAdvice
public class UnavailableServiceExceptionHandler {
    /**
     * Handles UnavailableServiceException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex The UnavailableServiceException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(UnavailableServiceException.class)
    public ResponseEntity<ErrorDetails> handleUnavailableServiceException(UnavailableServiceException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}