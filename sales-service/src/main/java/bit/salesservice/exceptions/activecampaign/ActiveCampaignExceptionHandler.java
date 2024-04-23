package bit.salesservice.exceptions.activecampaign;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles exceptions related to ActiveCampaign.
 * It provides a customized error response when an {@link ActiveCampaignException} is thrown.
 */
@ControllerAdvice
public class ActiveCampaignExceptionHandler {

    /**
     * This method is an exception handler for {@link ActiveCampaignException}.
     * It creates an error response with the provided details and returns it with a status code of 400 (Bad Request).
     *
     * @param ex the {@link ActiveCampaignException} that was thrown
     * @return a {@link ResponseEntity} containing the error details and a status code of 400
     */
    @ExceptionHandler(ActiveCampaignException.class)
    public ResponseEntity<ErrorDetails> handleActiveCampaignExceptionHandler(ActiveCampaignException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
