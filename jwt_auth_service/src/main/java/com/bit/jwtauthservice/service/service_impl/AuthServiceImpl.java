package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.config.PasswordEncoderConfig;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import com.bit.jwtauthservice.entity.ResetPasswordToken;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.badcredentials.BadCredentialsException;
import com.bit.jwtauthservice.exceptions.confirmpassword.ConfirmPasswordException;
import com.bit.jwtauthservice.exceptions.incorrectoldpassword.IncorrectOldPasswordException;
import com.bit.jwtauthservice.exceptions.passwordmismatch.PasswordMismatchException;
import com.bit.jwtauthservice.exceptions.resettokenexpiration.InvalidResetTokenException;
import com.bit.jwtauthservice.exceptions.samepassword.SamePasswordException;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.ResetPasswordTokenRepository;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.AuthService;
import com.bit.jwtauthservice.service.EmailService;
import com.bit.jwtauthservice.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final HttpServletRequest request;
    private static final String USER_NOT_FOUND = "User not found";

    @Value("${password.reset.link.body}")
    private String resetLinkBody;

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public LoginRes login(LoginReq loginReq) {
        logger.info("Attempting to authenticate user: {}", loginReq.getUserCode());

        User user = userRepository.findByUserCode(loginReq.getUserCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }

        if (!passwordEncoderConfig.passwordEncoder().matches(loginReq.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        logger.info("User '{}' authenticated successfully.", loginReq.getUserCode());

        return LoginRes
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void forgotUserCode(ForgotUserCodeReq forgotUserCodeReq) {
        logger.info("Sending forgotten user code to '{}'", forgotUserCodeReq.getEmail());

        User user = userRepository.findByEmail(forgotUserCodeReq.getEmail())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        emailService.sendUserCode(user.getEmail(), "Your user code.", "sendUserCode-mail-template", user.getUserCode());

        logger.info("User code sent.");
    }

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

        logger.info("Password changed successfully");
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userCode;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userCode = jwtService.extractUsername(refreshToken);
        if (userCode != null) {
            var user = this.userRepository.findByUserCode(userCode)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
//                revokeAllUserTokens(user);
//                saveUserToken(user, accessToken);
                var authResponse = LoginRes.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
