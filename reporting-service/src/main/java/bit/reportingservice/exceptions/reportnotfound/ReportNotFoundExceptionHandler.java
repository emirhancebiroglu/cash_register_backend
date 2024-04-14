package bit.reportingservice.exceptions.reportnotfound;

import bit.reportingservice.exceptions.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ReportNotFoundExceptionHandler {
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleReportNotFoundExceptionHandler(ReportNotFoundException ex){
        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
