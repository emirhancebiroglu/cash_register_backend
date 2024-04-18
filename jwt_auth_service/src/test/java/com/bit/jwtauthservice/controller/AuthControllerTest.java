package com.bit.jwtauthservice.controller;

import com.bit.jwtauthservice.dto.RefreshTokenReq;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import com.bit.jwtauthservice.entity.RefreshToken;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.service.AuthService;
import com.bit.jwtauthservice.service.JwtService;
import com.bit.jwtauthservice.service.RefreshTokenService;
import com.bit.jwtauthservice.utils.TokenStateChanger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenStateChanger tokenStateChanger;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        LoginReq loginReq = new LoginReq();
        LoginRes expectedLoginRes = new LoginRes();
        when(authService.login(loginReq)).thenReturn(expectedLoginRes);

        LoginRes actualLoginRes = authController.login(loginReq);

        assertEquals(expectedLoginRes, actualLoginRes);
        verify(authService, times(1)).login(loginReq);
    }

    @Test
    void forgotUserCode() {
        ForgotUserCodeReq forgotUserCodeReq = new ForgotUserCodeReq();

        ResponseEntity<String> response = authController.forgotUserCode(forgotUserCodeReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User code sent successfully!", response.getBody());
        verify(authService, times(1)).forgotUserCode(forgotUserCodeReq);
    }

    @Test
    void forgotPassword() {
        ForgotPasswordReq forgotPasswordReq = new ForgotPasswordReq();

        ResponseEntity<String> response = authController.forgotPassword(forgotPasswordReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset email sent successfully!", response.getBody());
        verify(authService, times(1)).forgotPassword(forgotPasswordReq);
    }

    @Test
    void resetPassword() {
        String token = "token";

        ResetPasswordReq resetPasswordReq = new ResetPasswordReq();

        ResponseEntity<String> response = authController.resetPassword(token, resetPasswordReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successfully!", response.getBody());
        verify(authService, times(1)).resetPassword(token, resetPasswordReq);
    }

    @Test
    void changePassword() {
        ChangePasswordReq changePasswordReq = new ChangePasswordReq();

        ResponseEntity<String> response = authController.changePassword(changePasswordReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully!", response.getBody());
        verify(authService, times(1)).changePassword(changePasswordReq);
    }

    @Test
    void validateToken() {
        String jwt = "testJwtToken";

        boolean expectedValidity = true;

        when(authService.validateToken(jwt)).thenReturn(Mono.just(expectedValidity));

        Mono<Boolean> actualValidity = authController.validateToken(jwt);

        assertEquals(expectedValidity, actualValidity.block());

        verify(authService, times(1)).validateToken(jwt);
    }

    @Test
    void refreshToken(){
        User user = new User();

        RefreshTokenReq refreshTokenReq = new RefreshTokenReq();
        refreshTokenReq.setRefreshToken("refreshToken");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenReq.getRefreshToken());
        refreshToken.setUser(user);

        when(refreshTokenService.findByToken("refreshToken")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtService.generateToken(user)).thenReturn("generatedAccessToken");

        doNothing().when(tokenStateChanger).revokeAllUserTokens(user);
        doNothing().when(tokenStateChanger).saveUserToken(user, "generatedAccessToken");

        LoginRes loginRes = authController.refreshToken(refreshTokenReq);

        assertNotNull(loginRes);
        assertEquals("generatedAccessToken", loginRes.getAccessToken());

        verify(refreshTokenService, times(1)).findByToken("refreshToken");
        verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
        verify(jwtService, times(1)).generateToken(user);
        verify(tokenStateChanger, times(1)).revokeAllUserTokens(user);
        verify(tokenStateChanger, times(1)).saveUserToken(user, "generatedAccessToken");
    }
}
