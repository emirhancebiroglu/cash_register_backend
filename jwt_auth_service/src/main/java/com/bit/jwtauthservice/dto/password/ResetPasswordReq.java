package com.bit.jwtauthservice.dto.password;

import lombok.Data;

@Data
public class ResetPasswordReq {
    private String newPassword;
    private String confirmPassword;
}
