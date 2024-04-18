package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface AuthService {
    /**
     * Authenticates a user with the provided credentials and returns login response.
     *
     * @param loginReq The login request containing user code and password.
     * @return The login response containing access and refresh tokens.
     */
    LoginRes login(@RequestBody LoginReq loginReq);

    /**
     * Sends a user code to the provided email address for user identification.
     *
     * @param forgotUserCodeReq The request containing the user's email.
     */
    void forgotUserCode(ForgotUserCodeReq forgotUserCodeReq);

    /**
     * Sends a password reset email to the provided email address.
     *
     * @param forgotPasswordReq The request containing the user's email.
     */
    void forgotPassword(ForgotPasswordReq forgotPasswordReq);

    /**
     * Resets the password using the provided reset token and new password.
     *
     * @param token             The reset token.
     * @param resetPasswordReq The request containing the new password.
     */
    void resetPassword(String token, ResetPasswordReq resetPasswordReq);

    /**
     * Changes the password for the authenticated user.
     *
     * @param changePasswordReq The request containing the old and new passwords.
     */
    void changePassword(ChangePasswordReq changePasswordReq);

    /**
     * Validates the authenticity and expiration of a JWT token.
     *
     * @param jwt The JWT token to validate.
     * @return A Mono indicating whether the token is valid.
     */
    Mono<Boolean> validateToken(String jwt);
}
