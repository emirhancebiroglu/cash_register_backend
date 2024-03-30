package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.entity.Token;
import com.bit.jwtauthservice.exceptions.tokennotfound.TokenNotFoundException;
import com.bit.jwtauthservice.repository.TokenRepository;
import com.bit.jwtauthservice.service.service_impl.LogoutServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogoutServiceTest {
    @InjectMocks
    private LogoutServiceImpl logoutService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logout_ShouldSetTokenExpiredAndRevoked() {
        String jwt = "sampleJwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(tokenRepository.findByJwtToken(jwt)).thenReturn(java.util.Optional.of(new Token()));

        logoutService.logout(request, response, authentication);

        verify(tokenRepository).findByJwtToken(jwt);
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void logout_ShouldThrowTokenNotFoundException_WhenTokenNotFound() {
        String jwt = "sampleJwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(tokenRepository.findByJwtToken(jwt)).thenReturn(java.util.Optional.empty());

        assertThrows(TokenNotFoundException.class, () -> logoutService.logout(request, response, authentication));

        verify(tokenRepository).findByJwtToken(jwt);
        verify(tokenRepository, never()).save(any(Token.class));
    }
}
