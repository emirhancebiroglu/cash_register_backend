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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * Body of the email to be sent for password reset link.
     */
    @Value("${password.reset.link.body}")
    private String resetLinkBody;

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param loginReq The login request containing user credentials.
     * @return LoginRes object containing access and refresh tokens upon successful authentication.
     * @throws UserNotFoundException    If the user with the given user code is not found.
     * @throws BadCredentialsException  If the provided password is incorrect.
     */
    @Override
    public LoginRes login(LoginReq loginReq) {
        logger.info("Attempting to authenticate user: {}", loginReq.getUserCode());

        RefreshToken refreshToken;

        User user = userRepository.findByUserCode(loginReq.getUserCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }

        if (!passwordEncoderConfig.passwordEncoder().matches(loginReq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var jwtToken = jwtService.generateToken(user);

        refreshToken = refreshTokenRepository.findByUserId(user.getId());

        if (refreshToken != null){
            refreshTokenRepository.delete(refreshToken);
        }

        refreshToken = refreshTokenService.createRefreshToken(user);

        tokenStateChanger.revokeAllUserTokens(user);
        tokenStateChanger.saveUserToken(user, jwtToken);

        logger.info("User '{}' authenticated successfully.", loginReq.getUserCode());

        return LoginRes
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * Sends the forgotten user code to the specified email address.
     *
     * @param forgotUserCodeReq The request containing the email address for sending the user code.
     * @throws UserNotFoundException If the user with the given email address is not found.
     */
    @Override
    public void forgotUserCode(ForgotUserCodeReq forgotUserCodeReq) {
        logger.info("Sending forgotten user code to '{}'", forgotUserCodeReq.getEmail());

        User user = userRepository.findByEmail(forgotUserCodeReq.getEmail())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        emailService.sendUserCode(user.getEmail(), "Your user code.", "sendUserCode-mail-template", user.getUserCode());

        logger.info("User code sent.");
    }

    /**
     * Initiates the process for resetting the user's password.
     *
     * @param forgotPasswordReq The request containing the email address for sending the password reset link.
     * @throws UserNotFoundException If the user with the given email address is not found.
     */
    @Override
    public void forgotPassword(ForgotPasswordReq forgotPasswordReq) {
        logger.info("Preparing the password reset link...");

        User user = userRepository.findByEmail(forgotPasswordReq.getEmail())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(UUID.randomUUID().toString());
        resetPasswordToken.setExpirationDate(LocalDate.now().plusDays(1));

        user.setResetPasswordToken(resetPasswordToken);

        resetPasswordTokenRepository.save(resetPasswordToken);
        userRepository.save(user);

        String resetLink = resetLinkBody + resetPasswordToken.getToken();

        emailService.sendPasswordResetEmail(forgotPasswordReq.getEmail(), "Reset Password", "resetPassword-mail-template", resetLink);

        logger.info("Password reset email sent.");
    }

    /**
     * Resets the password for the user using the provided token and new password.
     *
     * @param token             The token used for password reset.
     * @param resetPasswordReq The request containing the new password and confirmation.
     * @throws InvalidResetTokenException If the reset token is invalid or expired.
     * @throws PasswordMismatchException If the new password and confirmation do not match.
     * @throws SamePasswordException     If the new password is the same as the old password.
     * @throws UserNotFoundException    If the user associated with the reset token is not found.
     */
    @Override
    public void resetPassword(String token, ResetPasswordReq resetPasswordReq) {
        logger.info("Password resetting process is on...");

        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidResetTokenException("Invalid token"));

        if (resetPasswordToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new InvalidResetTokenException("Token has expired");
        }

        User user = userRepository.findByResetPasswordToken(resetPasswordToken)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!resetPasswordReq.getNewPassword().equals(resetPasswordReq.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (passwordEncoderConfig.passwordEncoder().matches(resetPasswordReq.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException("New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(resetPasswordReq.getNewPassword()));
        user.setResetPasswordToken(null);
        userRepository.save(user);

        resetPasswordTokenRepository.delete(resetPasswordToken);

        logger.info("Password reset successfully");
    }

    /**
     * Changes the password for the authenticated user.
     *
     * @param changePasswordReq The request containing the old and new passwords for password change.
     * @throws IncorrectOldPasswordException If the old password provided is incorrect.
     * @throws ConfirmPasswordException      If the new and confirmation passwords do not match.
     * @throws UserNotFoundException        If the authenticated user is not found.
     */
    @Override
    public void changePassword(ChangePasswordReq changePasswordReq) {
        logger.info("Changing the password...");

        String userCode = jwtService.extractUsername(request.getHeader("Authorization").substring(7));

        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!passwordEncoderConfig.passwordEncoder().matches(changePasswordReq.getOldPassword(), user.getPassword())) {
            throw new IncorrectOldPasswordException("Incorrect old password");
        }

        if (!changePasswordReq.getNewPassword().equals(changePasswordReq.getConfirmPassword())) {
            throw new ConfirmPasswordException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(changePasswordReq.getNewPassword()));
        userRepository.save(user);

        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        logoutHandler.logout(request, null, authentication);

        logger.info("Password changed successfully");
    }

    /**
     * Validates the JWT token's validity.
     *
     * @param jwt The JWT token to validate.
     * @return A Mono emitting a boolean value indicating the token's validity.
     * @throws TokenNotFoundException If the token is not found in the database.
     */
    @Override
    public Mono<Boolean> validateToken(String jwt) {
        logger.info("Validating token: {}", jwt);

        return Mono.fromCallable(() -> tokenRepository.findByJwtToken(jwt)
                        .orElseThrow(() -> new TokenNotFoundException("Token not found")))
                .subscribeOn(Schedulers.boundedElastic())
                .map(token -> !token.isExpired() && !token.isRevoked())
                .doOnSuccess(validity -> logger.info("Token validation result: {}", validity));
    }
}
