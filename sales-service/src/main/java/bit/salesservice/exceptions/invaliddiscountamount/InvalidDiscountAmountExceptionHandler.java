package bit.salesservice.exceptions.invaliddiscountamount;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles exceptions related to invalid discount amounts.
 * It is a controller advice that catches {@link InvalidDiscountAmountException} and returns a response entity with an error details object.
 */
@ControllerAdvice
public class InvalidDiscountAmountExceptionHandler {

    /**
     * This method handles the {@link InvalidDiscountAmountException} by creating an error details object and returning a response entity with it.
     *
     * @param ex the exception of type {@link InvalidDiscountAmountException}
     * @return a response entity containing an error details object
     */
    @ExceptionHandler(InvalidDiscountAmountException.class)
    public ResponseEntity<ErrorDetails> handleInvalidDiscountAmountExceptionHandler(InvalidDiscountAmountException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
