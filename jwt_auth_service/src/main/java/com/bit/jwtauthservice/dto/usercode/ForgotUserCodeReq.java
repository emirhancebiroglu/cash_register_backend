package com.bit.jwtauthservice.dto.usercode;

import lombok.Data;

/**
 * Data transfer object for handling requests to retrieve forgotten user codes.
 */
@Data
public class ForgotUserCodeReq {
    private String email;
}
