package com.bit.jwtauthservice.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDetails {
    /**
     * The HTTP status code.
     */
    private int statusCode;

    /**
     * The timestamp when the error occurred.
     */
    private LocalDateTime timeStamp;

    /**
     * The error message.
     */
    private String errorMessage;

    /**
     * The status description.
     */
    private String status;

    /**
     * Constructs an ErrorDetails object with the given parameters.
     *
     * @param statusCode   The HTTP status code.
     * @param timeStamp    The timestamp when the error occurred.
     * @param errorMessage The error message.
     * @param status       The status description.
     */
    public ErrorDetails(int statusCode, LocalDateTime timeStamp, String errorMessage, String status) {
        this.statusCode = statusCode;
        this.timeStamp = timeStamp;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}
