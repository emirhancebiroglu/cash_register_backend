package com.bit.productservice.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents the details of an error, including status code, timestamp, error message, and status.
 */
@Data
public class ErrorDetails {
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorMessage;
    private String status;

    /**
     * Constructs an ErrorDetails object with the provided parameters.
     *
     * @param statusCode    The status code of the error.
     * @param timeStamp     The timestamp when the error occurred.
     * @param errorMessage  The error message.
     * @param status        The status of the error.
     */
    public ErrorDetails(int statusCode, LocalDateTime timeStamp, String errorMessage, String status) {
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
        this.errorMessage = errorMessage;
        this.status = status;
    }


}
