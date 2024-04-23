package bit.salesservice.exceptions.fixedamountdiscounttypewithprovidedquantity;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class is a ControllerAdvice that handles exceptions of type {@link FixedAmountDiscountTypeWithProvidedQuantityException}.
 * It creates an {@link ErrorDetails} object with the appropriate HTTP status, timestamp, error message, and status name,
 * then returns a {@link ResponseEntity} containing the error details with the specified HTTP status.
 */
@ControllerAdvice
public class FixedAmountDiscountTypeWithProvidedQuantityExceptionHandler {

    /**
     * This method is an exception handler for {@link FixedAmountDiscountTypeWithProvidedQuantityException}.
     * It takes an instance of the exception as a parameter and creates an {@link ErrorDetails} object with the
     * following details: HTTP status code (400 - Bad Request), current timestamp, error message from the exception,
     * and the status name "BAD_REQUEST".
     *
     * @param ex the {@link FixedAmountDiscountTypeWithProvidedQuantityException} to handle
     * @return a {@link ResponseEntity} containing the error details and an HTTP status of 400 - Bad Request
     */
    @ExceptionHandler(FixedAmountDiscountTypeWithProvidedQuantityException.class)
    public ResponseEntity<ErrorDetails> handleFixedAmountDiscountTypeWithProvidedQuantityExceptionHandler(FixedAmountDiscountTypeWithProvidedQuantityException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
