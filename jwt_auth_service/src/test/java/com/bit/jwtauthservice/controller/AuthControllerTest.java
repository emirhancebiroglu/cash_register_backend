package com.bit.jwtauthservice.controller;

import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import com.bit.jwtauthservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;

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
    void refreshToken() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        authController.refreshToken(request, response);

        verify(authService, times(1)).refreshToken(request, response);
    }
}
