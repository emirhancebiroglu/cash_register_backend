package com.bit.jwtauthservice.dto.password;

import com.bit.jwtauthservice.annotations.ValidPassword;
import lombok.Data;

/**
 * Data transfer object for handling password reset requests.
 */
@Data
public class ResetPasswordReq {
    @ValidPassword
    private String newPassword;
    private String confirmPassword;
}
