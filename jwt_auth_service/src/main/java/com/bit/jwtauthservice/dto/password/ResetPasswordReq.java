package com.bit.jwtauthservice.dto.password;

import com.bit.jwtauthservice.annotations.ValidPassword;
import lombok.Data;

@Data
public class ResetPasswordReq {
    @ValidPassword
    private String newPassword;
    private String confirmPassword;
}
