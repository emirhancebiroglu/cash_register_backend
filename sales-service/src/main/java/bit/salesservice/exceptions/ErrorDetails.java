package bit.salesservice.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents details of an error that occurred in the application.
 */
@Data
public class ErrorDetails {
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorMessage;
    private String status;

    /**
     * Constructs an ErrorDetails object with the provided details.
     *
     * @param statusCode   The HTTP status code of the error.
     * @param timeStamp    The timestamp when the error occurred.
     * @param errorMessage The error message.
     * @param status       The status of the error.
     */
    public ErrorDetails(int statusCode, LocalDateTime timeStamp, String errorMessage, String status) {
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
        this.errorMessage = errorMessage;
        this.status = status;
    }


}
