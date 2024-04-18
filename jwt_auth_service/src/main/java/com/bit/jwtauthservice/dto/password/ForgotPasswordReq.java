package com.bit.jwtauthservice.dto.password;

import lombok.Data;

/**
 * Data transfer object for handling forgot password requests.
 */
@Data
public class ForgotPasswordReq {
    private String email;
}
