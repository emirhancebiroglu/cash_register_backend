package com.bit.jwtauthservice.dto.password;

import com.bit.jwtauthservice.annotations.ValidPassword;
import lombok.Data;

/**
 * Data transfer object for handling change password requests.
 */
@Data
public class ChangePasswordReq {
    private String oldPassword;
    @ValidPassword
    private String newPassword;
    private String confirmPassword;
}
