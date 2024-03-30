package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.config.PasswordEncoderConfig;
import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
import com.bit.jwtauthservice.entity.ResetPasswordToken;
import com.bit.jwtauthservice.entity.Token;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.badcredentials.BadCredentialsException;
import com.bit.jwtauthservice.exceptions.confirmpassword.ConfirmPasswordException;
import com.bit.jwtauthservice.exceptions.incorrectoldpassword.IncorrectOldPasswordException;
import com.bit.jwtauthservice.exceptions.invalidrefreshtoken.InvalidRefreshTokenException;
import com.bit.jwtauthservice.exceptions.passwordmismatch.PasswordMismatchException;
import com.bit.jwtauthservice.exceptions.resettokenexpiration.InvalidResetTokenException;
import com.bit.jwtauthservice.exceptions.samepassword.SamePasswordException;
import com.bit.jwtauthservice.exceptions.tokennotfound.TokenNotFoundException;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.ResetPasswordTokenRepository;
import com.bit.jwtauthservice.repository.TokenRepository;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.service_impl.AuthServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoderConfig passwordEncoderConfig;

    @Getter
    @Mock
    private TokenRepository tokenRepository;

    @Mock
    @Getter
    private LogoutHandler logoutHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthServiceImpl authService;

    private ResetPasswordToken resetPasswordToken;
    private User user;
    private PasswordEncoder passwordEncoder;
    private ResetPasswordReq resetPasswordReq;
    private ChangePasswordReq changePasswordReq;
    private String userCode;
    private String password;

    @BeforeEach
    void setUp() {



        resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken("token");
        resetPasswordToken.setExpirationDate(LocalDate.now().plusDays(1));

        passwordEncoder = mock(PasswordEncoder.class);

        userCode = "userCode";
        password = "password";

        user = new User();
        user.setEmail("test@example.com");
        user.setResetPasswordToken(resetPasswordToken);
        user.setPassword(password);
        user.setUserCode(userCode);

        resetPasswordReq = new ResetPasswordReq();
        resetPasswordReq.setConfirmPassword(password);
        resetPasswordReq.setNewPassword(password);

        changePasswordReq = new ChangePasswordReq();
        changePasswordReq.setOldPassword(password);
        changePasswordReq.setNewPassword("newPassword");
        changePasswordReq.setConfirmPassword("newPassword");
    }

    @Test
    void login(){
        LoginReq loginReq = new LoginReq(userCode, password);

        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword())).thenReturn(true);
        String jwt = "jwt";
        when(jwtService.generateToken(user)).thenReturn(jwt);

        LoginRes loginRes = authService.login(loginReq);

        assertEquals(jwt, loginRes.getAccessToken());
        verify(userRepository, times(1)).findByUserCode(userCode);
        verify(passwordEncoderConfig, times(2)).passwordEncoder();
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void login_invalidUser_throwsUserNotFoundException(){
        LoginReq loginReq = new LoginReq(userCode, password);

        assertThrows(UserNotFoundException.class, () -> authService.login(loginReq));

        verify(userRepository, times(1)).findByUserCode(userCode);
    }

    @Test
    void login_shouldThrowUserNotFoundException_whenUserIsDeleted() {
        LoginReq loginReq = new LoginReq(userCode, password);

        user.setDeleted(true);

        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));

        assertThrows(UserNotFoundException.class, () -> authService.login(loginReq));
    }

    @Test
    void login_shouldThrowBadCredentialsException_whenPasswordIsInvalid() {
        LoginReq loginReq = new LoginReq(userCode, password);

        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(loginReq));
    }

    @Test
    void forgotUserCode_shouldSendAnEmailWithTheUserCodeAndLog_whenTheEmailExistsInTheDatabase() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ForgotUserCodeReq forgotUserCodeReq = new ForgotUserCodeReq();
        forgotUserCodeReq.setEmail(email);

        authService.forgotUserCode(forgotUserCodeReq);

        verify(emailService).sendUserCode(forgotUserCodeReq.getEmail(), "Your user code.", "sendUserCode-mail-template", user.getUserCode());
    }

    @Test
    void forgotUserCode_shouldThrowAnException_whenTheEmailDoesNotExistInTheDatabase() {
        String email = "nonexistent@example.com";

        ForgotUserCodeReq forgotUserCodeReq = new ForgotUserCodeReq();
        forgotUserCodeReq.setEmail(email);

        assertThrows(UserNotFoundException.class, () -> authService.forgotUserCode(forgotUserCodeReq));
    }

    @Test
    void forgotPassword_shouldSendEmailWithResetLink_whenUserExists() {
    String email = "<EMAIL>";
    ForgotPasswordReq forgotPasswordReq = new ForgotPasswordReq();
    forgotPasswordReq.setEmail(email);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

    authService.forgotPassword(forgotPasswordReq);

    verify(emailService).sendPasswordResetEmail(eq(email), anyString(), anyString(), anyString());
    }

    @Test
    void forgotPassword_invalidUser_userNotFoundException() {
        String email = "<EMAIL>";
        ForgotPasswordReq forgotPasswordReq = new ForgotPasswordReq();
        forgotPasswordReq.setEmail(email);

        assertThrows(UserNotFoundException.class, () -> authService.forgotPassword(forgotPasswordReq));
    }

    @Test
    void resetPassword_InvalidToken_ShouldThrowInvalidResetTokenException() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidResetTokenException.class, this::resetPasswordCall);

        verify(resetPasswordTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    void resetPassword_ExpiredToken_ShouldThrowInvalidResetTokenException() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.ofNullable(resetPasswordToken));
        resetPasswordToken.setExpirationDate(LocalDate.now().minusDays(1));

        assertThrows(InvalidResetTokenException.class, this::resetPasswordCall);

        verify(resetPasswordTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    void resetPassword_PasswordsDoNotMatch_ShouldThrowPasswordMismatchException() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.ofNullable(resetPasswordToken));

        when(userRepository.findByResetPasswordToken(any())).thenReturn(Optional.of(user));
        resetPasswordReq.setConfirmPassword("diffPassword");

        assertThrows(PasswordMismatchException.class, () -> authService.resetPassword("token", resetPasswordReq));

        verify(resetPasswordTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    void resetPassword_NewPasswordSameAsOldPassword_ShouldThrowSamePasswordException() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.ofNullable(resetPasswordToken));

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);

        when(userRepository.findByResetPasswordToken(any())).thenReturn(Optional.of(user));

        when(passwordEncoderConfig.passwordEncoder().matches(resetPasswordReq.getNewPassword(), user.getPassword())).thenReturn(true);

        assertThrows(SamePasswordException.class, () -> authService.resetPassword("token", resetPasswordReq));

        verify(resetPasswordTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    void resetPassword_Success_ShouldUpdateUserAndDeleteToken() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.ofNullable(resetPasswordToken));
        when(userRepository.findByResetPasswordToken(any(ResetPasswordToken.class))).thenReturn(Optional.ofNullable(user));

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(resetPasswordReq.getNewPassword())).thenReturn("encoded_password");

        authService.resetPassword("token", resetPasswordReq);

        verify(userRepository, times(1)).findByResetPasswordToken(any(ResetPasswordToken.class));
        verify(userRepository, times(1)).save(user);
        verify(resetPasswordTokenRepository, times(1)).delete(resetPasswordToken);
    }

    @Test
    void resetPassword_invalidUser_userNotFoundException() {
        when(resetPasswordTokenRepository.findByToken(anyString())).thenReturn(Optional.ofNullable(resetPasswordToken));

        assertThrows(UserNotFoundException.class, () -> authService.resetPassword("token", resetPasswordReq));

        verify(userRepository).findByResetPasswordToken(resetPasswordToken);
    }

    @Test
    void changePassword_correctOldPassword_passwordChanged() {
        String newPassword = "newPassword";

        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtService.extractUsername("someToken")).thenReturn(userCode);

        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-password");
        when(passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword())).thenReturn(true);

        authService.changePassword(changePasswordReq);

        verify(userRepository).findByUserCode(userCode);
        verify(passwordEncoderConfig.passwordEncoder()).encode(newPassword);
        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void changePassword_incorrectOldPassword_incorrectOldPasswordException() {
        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtService.extractUsername("someToken")).thenReturn(userCode);
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword())).thenReturn(false);

        assertThrows(IncorrectOldPasswordException.class, () -> authService.changePassword(changePasswordReq));

        verify(userRepository).findByUserCode(userCode);
        verifyNoMoreInteractions(passwordEncoderConfig.passwordEncoder());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePassword_newAndConfirmPasswordsNotMatch_confirmPasswordException() {
        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtService.extractUsername("someToken")).thenReturn(userCode);

        changePasswordReq.setConfirmPassword("<PASSWORD>");

        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoderConfig.passwordEncoder().matches(password, user.getPassword())).thenReturn(true);

        assertThrows(ConfirmPasswordException.class, () -> authService.changePassword(changePasswordReq));

        verify(userRepository).findByUserCode(userCode);
        verifyNoMoreInteractions(passwordEncoderConfig.passwordEncoder());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePassword_invalidUser_userNotFoundException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtService.extractUsername("someToken")).thenReturn(userCode);

        changePasswordReq.setConfirmPassword("<PASSWORD>");

        assertThrows(UserNotFoundException.class, () -> authService.changePassword(changePasswordReq));

        verify(userRepository).findByUserCode(userCode);
    }

    @Test
    void refreshToken() throws IOException {
        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        List<Token> validUserTokens = Collections.singletonList(new Token());

        when(response.getOutputStream()).thenReturn(outputStream);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer refreshToken");
        when(jwtService.extractUsername("refreshToken")).thenReturn(userCode);
        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("refreshToken", user)).thenReturn(true);
        when(tokenRepository.findAllValidTokensByUser(user.getId())).thenReturn(validUserTokens);

        authService.refreshToken(request, response);

        verify(jwtService).extractUsername("refreshToken");
        verify(userRepository).findByUserCode(userCode);
        verify(jwtService).isTokenValid("refreshToken", user);
        verify(jwtService).generateToken(user);
    }

    @Test
    void refreshToken_InvalidRefreshToken_ThrowsInvalidRefreshTokenException() throws IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer refreshToken");
        when(jwtService.extractUsername("refreshToken")).thenReturn(userCode);
        when(userRepository.findByUserCode(userCode)).thenReturn(Optional.of(user));

        when(jwtService.isTokenValid("refreshToken", user)).thenReturn(false);

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(request, response));

        verify(response, never()).getOutputStream();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void validateToken_ValidToken() {
        String jwt = "validJwtToken";
        Token token = new Token();
        token.setExpired(false);
        token.setRevoked(false);

        when(tokenRepository.findByJwtToken(jwt)).thenReturn(Optional.of(token));

        Mono<Boolean> result = authService.validateToken(jwt);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(tokenRepository, times(1)).findByJwtToken(jwt);
    }

    @Test
    void validateToken_InvalidToken() {
        String jwt = "invalidJwtToken";

        when(tokenRepository.findByJwtToken(jwt)).thenReturn(Optional.empty());

        Mono<Boolean> result = authService.validateToken(jwt);

        StepVerifier.create(result)
                .expectError(TokenNotFoundException.class)
                .verify();

        verify(tokenRepository, times(1)).findByJwtToken(jwt);
    }

    private void resetPasswordCall() {
        authService.resetPassword("token", new ResetPasswordReq());
    }
}
