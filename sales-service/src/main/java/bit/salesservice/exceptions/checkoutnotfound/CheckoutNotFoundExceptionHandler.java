package bit.salesservice.exceptions.checkoutnotfound;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles the {@link CheckoutNotFoundException} by creating an {@link ErrorDetails} object and returning a {@link ResponseEntity} with the error details.
 */
@ControllerAdvice
public class CheckoutNotFoundExceptionHandler {

    /**
     * This method is an exception handler for {@link CheckoutNotFoundException}. It creates an {@link ErrorDetails} object with the appropriate HTTP status code, timestamp, error message, and error name.
     *
     * @param ex The {@link CheckoutNotFoundException} that triggered this method.
     * @return A {@link ResponseEntity} containing the {@link ErrorDetails} object.
     */
    @ExceptionHandler(CheckoutNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleCheckoutNotFoundExceptionHandler(CheckoutNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
