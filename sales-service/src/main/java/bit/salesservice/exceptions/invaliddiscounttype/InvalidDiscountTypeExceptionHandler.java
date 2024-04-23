package bit.salesservice.exceptions.invaliddiscounttype;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles exceptions related to invalid discount types.
 * It is a controller advice that catches {@link InvalidDiscountTypeException} and returns a response entity with an error details object.
 */
@ControllerAdvice
public class InvalidDiscountTypeExceptionHandler {

    /**
     * This method handles the {@link InvalidDiscountTypeException} by creating an error details object and returning a response entity with it.
     *
     * @param ex the exception of type {@link InvalidDiscountTypeException}
     * @return a response entity containing an error details object
     */
    @ExceptionHandler(InvalidDiscountTypeException.class)
    public ResponseEntity<ErrorDetails> handleInvalidDiscountTypeExceptionHandler(InvalidDiscountTypeException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
