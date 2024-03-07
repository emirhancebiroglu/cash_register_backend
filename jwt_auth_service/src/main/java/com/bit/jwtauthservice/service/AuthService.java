package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    LoginRes login(@RequestBody LoginReq loginReq);
    void forgotUserCode(ForgotUserCodeReq forgotUserCodeReq);
    void forgotPassword(ForgotPasswordReq forgotPasswordReq);
    void resetPassword(String token, ResetPasswordReq resetPasswordReq);
    void changePassword(ChangePasswordReq changePasswordReq);
}
