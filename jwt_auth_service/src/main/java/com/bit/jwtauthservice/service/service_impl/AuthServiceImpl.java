package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.config.PasswordEncoderConfig;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import com.bit.jwtauthservice.entity.RefreshToken;
import com.bit.jwtauthservice.entity.ResetPasswordToken;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.badcredentials.BadCredentialsException;
import com.bit.jwtauthservice.exceptions.confirmpassword.ConfirmPasswordException;
import com.bit.jwtauthservice.exceptions.incorrectoldpassword.IncorrectOldPasswordException;
import com.bit.jwtauthservice.exceptions.passwordmismatch.PasswordMismatchException;
import com.bit.jwtauthservice.exceptions.resettokenexpiration.InvalidResetTokenException;
import com.bit.jwtauthservice.exceptions.samepassword.SamePasswordException;
import com.bit.jwtauthservice.exceptions.tokennotfound.TokenNotFoundException;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.RefreshTokenRepository;
import com.bit.jwtauthservice.repository.ResetPasswordTokenRepository;
import com.bit.jwtauthservice.repository.TokenRepository;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.AuthService;
import com.bit.jwtauthservice.service.EmailService;
import com.bit.jwtauthservice.service.JwtService;
import com.bit.jwtauthservice.service.RefreshTokenService;
import com.bit.jwtauthservice.utils.TokenStateChanger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementation of the AuthService interface providing authentication and authorization functionality.
 * This service class handles user authentication, password management, token generation and validation,
 * and related operations.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final HttpServletRequest request;
    private final TokenRepository tokenRepository;
    private final LogoutHandler logoutHandler;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenStateChanger tokenStateChanger;
    private static final String USER_NOT_FOUND = "User not found";
    private final Logger logger = LogManager.getLogger(AuthServiceImpl.class);
    @Value("${password.reset.link.body}")
    private String resetLinkBody;

    @Override
    public LoginRes login(LoginReq loginReq) {
        logger.trace("Attempting to authenticate user: {}", loginReq.getUserCode());

        RefreshToken refreshToken;

        // Retrieving user by user code
        User user = userRepository.findByUserCode(loginReq.getUserCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // Checking if the user is deleted
        if (user.isDeleted()) {
            logger.error(USER_NOT_FOUND);
            throw new UserNotFoundException(USER_NOT_FOUND);
        }

        // Validating the password
        if (!passwordEncoderConfig.passwordEncoder().matches(loginReq.getPassword(), user.getPassword())) {
            logger.error("Invalid password");
            throw new BadCredentialsException("Invalid password");
        }

        // Generating JWT token
        var jwtToken = jwtService.generateToken(user);
        logger.debug("JWT token generated successfully: {}", jwtToken);

        // Deleting existing refresh token if present
        refreshToken = refreshTokenRepository.findByUserId(user.getId());
        if (refreshToken != null){
            refreshTokenRepository.delete(refreshToken);
        }

        // Creating new refresh token
        refreshToken = refreshTokenService.createRefreshToken(user);
        logger.debug("Refresh token created successfully: {}", refreshToken);

        // Revoking all existing user tokens and saving the new JWT token
        tokenStateChanger.revokeAllUserTokens(user);
        tokenStateChanger.saveUserToken(user, jwtToken);

        logger.trace("User '{}' authenticated successfully.", loginReq.getUserCode());

        return LoginRes
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public void forgotUserCode(ForgotUserCodeReq forgotUserCodeReq) {
        logger.trace("Sending forgotten user code to '{}'", forgotUserCodeReq.getEmail());

        // Retrieving user by email
        User user = userRepository.findByEmail(forgotUserCodeReq.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", forgotUserCodeReq.getEmail());
                    return new UserNotFoundException(USER_NOT_FOUND);
                });

        // Sending the user code via email
        emailService.sendUserCode(user.getEmail(), "Your user code.", "sendUserCode-mail-template", user.getUserCode());

        logger.trace("User code sent.");
    }

    @Override
    public void forgotPassword(ForgotPasswordReq forgotPasswordReq) {
        logger.trace("Preparing the password reset link...");

        // Retrieving user by email
        User user = userRepository.findByEmail(forgotPasswordReq.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", forgotPasswordReq.getEmail());
                    return new UserNotFoundException(USER_NOT_FOUND);
                });

        // Creating a reset password token
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(UUID.randomUUID().toString());
        resetPasswordToken.setExpirationDate(LocalDate.now().plusDays(1));

        // Associating the reset password token with the user
        user.setResetPasswordToken(resetPasswordToken);

        // Saving the reset password token and updating the user
        resetPasswordTokenRepository.save(resetPasswordToken);
        userRepository.save(user);

        // Constructing the reset password link
        String resetLink = resetLinkBody + resetPasswordToken.getToken();
        logger.debug("Reset password link generated: {}", resetLink);

        // Sending the password reset email
        emailService.sendPasswordResetEmail(forgotPasswordReq.getEmail(), "Reset Password", "resetPassword-mail-template", resetLink);

        logger.trace("Password reset email sent.");
    }

    @Override
    public void resetPassword(String token, ResetPasswordReq resetPasswordReq) {
        logger.trace("Password resetting process is on...");

        // Retrieving the reset password token from the repository
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid token {}", token);
                    return new InvalidResetTokenException("Invalid token");
                });

        // Checking if the reset token has expired
        if (resetPasswordToken.getExpirationDate().isBefore(LocalDate.now())) {
            logger.error("Token has expired {}", token);
            throw new InvalidResetTokenException("Token has expired");
        }

        // Retrieving the user associated with the reset token
        User user = userRepository.findByResetPasswordToken(resetPasswordToken)
                .orElseThrow(() -> {
                    logger.error("User not found for this token: {}", resetPasswordToken);
                    return new UserNotFoundException(USER_NOT_FOUND);
                });

        // Validating new password and confirm password match
        if (!resetPasswordReq.getNewPassword().equals(resetPasswordReq.getConfirmPassword())) {
            logger.error("Passwords do not match");
            throw new PasswordMismatchException("Passwords do not match");
        }

        // Ensuring the new password is different from the old password
        if (passwordEncoderConfig.passwordEncoder().matches(resetPasswordReq.getNewPassword(), user.getPassword())) {
            logger.error("New password cannot be the same as the old password");
            throw new SamePasswordException("New password cannot be the same as the old password");
        }

        // Encrypting and updating the new password, clearing the reset token
        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(resetPasswordReq.getNewPassword()));
        user.setResetPasswordToken(null);
        userRepository.save(user);

        // Deleting the used reset token
        resetPasswordTokenRepository.delete(resetPasswordToken);

        logger.trace("Password reset successfully");
    }

    @Override
    public void changePassword(ChangePasswordReq changePasswordReq) {
        logger.trace("Changing the password...");

        // Extracting user code from the JWT token
        String userCode = jwtService.extractUsername(request.getHeader("Authorization").substring(7));

        // Retrieving user details from the repository
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> {
                    logger.error("User not found for user code: {}", userCode);
                    return new UserNotFoundException(USER_NOT_FOUND);
                });

        // Validating old password
        if (!passwordEncoderConfig.passwordEncoder().matches(changePasswordReq.getOldPassword(), user.getPassword())) {
            logger.error("Incorrect old password");
            throw new IncorrectOldPasswordException("Incorrect old password");
        }

        // Validating new and confirm passwords match
        if (!changePasswordReq.getNewPassword().equals(changePasswordReq.getConfirmPassword())) {
            logger.error("New password and confirm password do not match");
            throw new ConfirmPasswordException("New password and confirm password do not match");
        }

        // Encrypting and updating the new password
        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(changePasswordReq.getNewPassword()));
        userRepository.save(user);

        // Logging out the user after password change
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        logoutHandler.logout(request, null, authentication);

        logger.trace("Password changed successfully");
    }

    @Override
    public Mono<Boolean> validateToken(String jwt) {
        logger.trace("Validating token: {}", jwt);

        // Create a Mono that asynchronously retrieves the token from the repository
        return Mono.fromCallable(() -> tokenRepository.findByJwtToken(jwt)
                        .orElseThrow(() -> {
                            logger.error("Token not found");
                            return new TokenNotFoundException("Token not found");
                        }))
                // Perform the operation on a bounded elastic scheduler
                .subscribeOn(Schedulers.boundedElastic())
                // Map the retrieved token to a boolean indicating its validity
                .map(token -> !token.isExpired() && !token.isRevoked())
                // Log the result of token validation
                .doOnSuccess(validity -> logger.trace("Token validation result: {}", validity));
    }
}
