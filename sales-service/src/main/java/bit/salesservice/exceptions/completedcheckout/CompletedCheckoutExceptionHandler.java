package bit.salesservice.exceptions.completedcheckout;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles exceptions related to completed checkout.
 * It is a controller advice that catches {@link CompletedCheckoutException} and returns an error response.
 */
@ControllerAdvice
public class CompletedCheckoutExceptionHandler {

    /**
     * This method handles the {@link CompletedCheckoutException} by creating an error response.
     * It takes a {@link CompletedCheckoutException} as a parameter and returns a {@link ResponseEntity} containing an {@link ErrorDetails} object.
     *
     * @param ex the {@link CompletedCheckoutException} to be handled
     * @return a {@link ResponseEntity} containing an {@link ErrorDetails} object with the error details
     */
    @ExceptionHandler(CompletedCheckoutException.class)
    public ResponseEntity<ErrorDetails> handleCompletedCheckoutExceptionHandler(CompletedCheckoutException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
