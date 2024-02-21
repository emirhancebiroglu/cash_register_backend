package com.bit.user_management_service.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDetails {
    private int statusCode;
    private LocalDateTime timeStamp;
    private String errorMessage;
    private String status;
}
