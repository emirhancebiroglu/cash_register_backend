package bit.salesservice.exceptions.campaignalreadyexists;

import bit.salesservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * This class handles the {@link CampaignAlreadyExistsException} by creating an error response.
 * It is a {@link ControllerAdvice} class, which means it can handle exceptions thrown from any controller.
 */
@ControllerAdvice
public class CampaignAlreadyExistsExceptionHandler {

    /**
     * This method is an exception handler for {@link CampaignAlreadyExistsException}.
     * It creates an error response with the provided exception details.
     *
     * @param ex the {@link CampaignAlreadyExistsException} to handle
     * @return a {@link ResponseEntity} containing the error details
     */
    @ExceptionHandler(CampaignAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleCampaignAlreadyExistsExceptionHandler(CampaignAlreadyExistsException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
