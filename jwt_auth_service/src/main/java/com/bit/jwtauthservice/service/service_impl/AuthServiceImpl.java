package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.config.PasswordEncoderConfig;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import com.bit.jwtauthservice.entity.ResetPasswordToken;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.badcredentials.BadCredentialsException;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.ResetPasswordTokenRepository;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.AuthService;
import com.bit.jwtauthservice.service.EmailService;
import com.bit.jwtauthservice.service.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Data
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private static final String USER_NOT_FOUND = "User not found";

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public LoginRes login(LoginReq loginReq) {
        logger.info("Attempting to authenticate user: {}", loginReq.getUserCode());

        User user = userRepository.findByUserCode(loginReq.getUserCode())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (user.isDeleted()){
            throw new UserNotFoundException(USER_NOT_FOUND);
        }

        if (!isPasswordValid(user, loginReq.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        var jwt = jwtService.generateToken(user);

        logger.info("User '{}' authenticated successfully.", loginReq.getUserCode());

        return LoginRes.builder().token(jwt).build();
    }

    @Override
    public void forgotUserCode(ForgotUserCodeReq forgotUserCodeReq) {
        User user = userRepository.findByEmail(forgotUserCodeReq.getEmail())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        emailService.sendUserCode(user.getEmail(), "Your user code.", "sendUserCode-mail-template", user.getUserCode());
    }

    @Override
    public void forgotPassword(ForgotPasswordReq forgotPasswordReq) {
        User user = userRepository.findByEmail(forgotPasswordReq.getEmail())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(UUID.randomUUID().toString());
        resetPasswordToken.setExpirationDate(LocalDate.now().plusDays(1));

        user.setResetPasswordToken(resetPasswordToken);

        resetPasswordTokenRepository.save(resetPasswordToken);
        userRepository.save(user);

        String resetLink = "http://localhost:8881/api/auth/reset-password?token=" + resetPasswordToken.getToken();

        emailService.sendPasswordResetEmail(forgotPasswordReq.getEmail(), "Reset Password", "resetPassword-mail-template", resetLink);
    }

    @Override
    public void resetPassword(String token, ResetPasswordReq resetPasswordReq) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetPasswordToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        User user = userRepository.findByResetPasswordToken(resetPasswordToken)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!resetPasswordReq.getNewPassword().equals(resetPasswordReq.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (passwordEncoderConfig.passwordEncoder().matches(resetPasswordReq.getNewPassword(), user.getPassword())){
            throw new IllegalArgumentException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(resetPasswordReq.getNewPassword()));
        user.setResetPasswordToken(null);
        userRepository.save(user);

        resetPasswordTokenRepository.delete(resetPasswordToken);
    }

    @Override
    public void changePassword(ChangePasswordReq changePasswordReq) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userCode = authentication.getName();

        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (!passwordEncoderConfig.passwordEncoder().matches(changePasswordReq.getOldPassword(), user.getPassword())){
            throw new BadCredentialsException("Incorrect old password");
        }

        if (!changePasswordReq.getNewPassword().equals(changePasswordReq.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoderConfig.passwordEncoder().encode(changePasswordReq.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isPasswordValid(User user, String password) {
        return passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword());
    }
}
