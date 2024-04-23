package bit.salesservice.exceptions.campaignnotfound;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles the CampaignNotFoundException by creating an ErrorDetails object and returning a ResponseEntity with the error details.
 */
@ControllerAdvice
public class CampaignNotFoundExceptionHandler {

    /**
     * This method is an exception handler for CampaignNotFoundException. It creates an ErrorDetails object with the appropriate status code, timestamp, error message, and status name.
     * Then, it returns a ResponseEntity containing the ErrorDetails object with the specified HTTP status NOT_FOUND.
     *
     * @param ex The CampaignNotFoundException that triggered this handler.
     * @return A ResponseEntity containing the ErrorDetails object with the specified HTTP status NOT_FOUND.
     */
    @ExceptionHandler(CampaignNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleCampaignNotFoundExceptionHandler(CampaignNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
