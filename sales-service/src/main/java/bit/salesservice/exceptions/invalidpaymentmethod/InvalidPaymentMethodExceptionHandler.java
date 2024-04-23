package bit.salesservice.exceptions.invalidpaymentmethod;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for InvalidPaymentMethodException.
 */
@ControllerAdvice
public class InvalidPaymentMethodExceptionHandler {
    /**
     * Handles InvalidPaymentMethodException and returns an appropriate ResponseEntity with error details.
     *
     * @param ex The InvalidPaymentMethodException to handle.
     * @return A ResponseEntity containing error details and HTTP status code.
     */
    @ExceptionHandler(InvalidPaymentMethodException.class)
    public ResponseEntity<ErrorDetails> handleInvalidPaymentMethodExceptionHandler(InvalidPaymentMethodException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
