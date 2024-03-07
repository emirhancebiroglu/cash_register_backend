package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.config.PasswordEncoderConfig;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.badcredentials.BadCredentialsException;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.AuthService;
import com.bit.jwtauthservice.service.EmailService;
import com.bit.jwtauthservice.service.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Data
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
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
    public void forgotUserCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        emailService.sendUserCode(user.getEmail(), "Your user code.", "sendUserCode-mail-template", user.getUserCode());
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        String resetToken = generateResetToken();
        

        String resetLink = "http://localhost:8881/api/auth/reset-password?token=" + resetToken;
        emailService.sendPasswordResetEmail(user.getEmail(), "Password Reset", "resetPassword-mail-template", resetLink);
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isPasswordValid(User user, String password) {
        return passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword());
    }
}
