package bit.salesservice.exceptions.productnotfound;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

/**
 * Global exception handler for ProductNotFoundException.
 */
@ControllerAdvice
public class ProductNotFoundExceptionHandler {
    /**
     * Handles ProductNotFoundException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex The ProductNotFoundException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleProductNotFoundException(ProductNotFoundException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
