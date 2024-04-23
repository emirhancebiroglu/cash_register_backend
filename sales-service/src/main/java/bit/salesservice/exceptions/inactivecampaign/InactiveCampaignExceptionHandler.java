package bit.salesservice.exceptions.inactivecampaign;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles exceptions related to inactive campaigns.
 * It provides a customized response for such exceptions.
 */
@ControllerAdvice
public class InactiveCampaignExceptionHandler {

    /**
     * This method is an exception handler for {@link InactiveCampaignException}.
     * It creates an {@link ErrorDetails} object with the appropriate status code, timestamp, error message, and status name.
     * Then, it returns a {@link ResponseEntity} containing the error details with the specified status code.
     *
     * @param ex The {@link InactiveCampaignException} that triggered this handler.
     * @return A {@link ResponseEntity} containing the error details and the specified status code.
     */
    @ExceptionHandler(InactiveCampaignException.class)
    public ResponseEntity<ErrorDetails> handleInactiveCampaignExceptionHandler(InactiveCampaignException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
