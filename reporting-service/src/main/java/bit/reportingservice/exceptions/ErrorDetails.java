package bit.reportingservice.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents details of an error.
 */
@Data
public class ErrorDetails {
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorMessage;
    private String status;

    /**
     * Constructs an ErrorDetails object.
     * @param statusCode HTTP status code of the error.
     * @param timeStamp Time stamp when the error occurred.
     * @param errorMessage Error message.
     * @param status Status of the error.
     */
    public ErrorDetails(int statusCode, LocalDateTime timeStamp, String errorMessage, String status) {
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}
