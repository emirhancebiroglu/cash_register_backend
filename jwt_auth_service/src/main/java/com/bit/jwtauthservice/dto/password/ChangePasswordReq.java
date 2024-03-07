package com.bit.jwtauthservice.dto.password;

import lombok.Data;

@Data
public class ChangePasswordReq {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
