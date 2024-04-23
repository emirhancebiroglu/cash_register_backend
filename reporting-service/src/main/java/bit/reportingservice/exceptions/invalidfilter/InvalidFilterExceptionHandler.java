package bit.reportingservice.exceptions.invalidfilter;

import bit.reportingservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controller advice to handle exceptions of type InvalidFilterException.
 */
@ControllerAdvice
public class InvalidFilterExceptionHandler {
    /**
     * Handles InvalidFilterException and returns an appropriate response entity.
     * @param ex The exception to handle.
     * @return ResponseEntity containing error details.
     */
    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ErrorDetails> handleInvalidFilterExceptionHandler(InvalidFilterException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
