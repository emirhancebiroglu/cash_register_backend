package com.bit.usermanagementservice.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The ErrorDetails class represents details of an error that occurred during an operation.
 * It includes information such as status code, timestamp, error message, and status.
 */
@Data
public class ErrorDetails {
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorMessage;
    private String status;

    /**
     * Constructs a new ErrorDetails instance with the specified details.
     *
     * @param statusCode the HTTP status code associated with the error.
     * @param timeStamp the timestamp when the error occurred.
     * @param errorMessage the detailed error message.
     * @param status the status description.
     */
    public ErrorDetails(int statusCode, LocalDateTime timeStamp, String errorMessage, String status) {
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
        this.errorMessage = errorMessage;
        this.status = status;
    }


}
