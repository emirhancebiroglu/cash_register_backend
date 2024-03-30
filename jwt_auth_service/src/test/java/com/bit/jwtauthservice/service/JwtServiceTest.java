package com.bit.jwtauthservice.service;


import com.bit.jwtauthservice.service.service_impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {
    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    private String token;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", "ea8419c9befbc532a8219250e11192e6bbd693e5a5c76c60135d8551ee6ee059");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 3600000L);

        userDetails = new User("testUser",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

        token = jwtService.generateToken(userDetails);
        refreshToken = jwtService.generateRefreshToken(userDetails);
    }

    @Test
    void generateToken_WithUserDetails_ShouldGenerateToken() {
        assertNotNull(token);
    }

    @Test
    void generateRefreshToken_WithUserDetails_ShouldGenerateToken() {
        assertNotNull(refreshToken);
    }

    @Test
    void extractUsername_ShouldReturnUsername() {
        String expectedUsername = "testUser";

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(expectedUsername, extractedUsername);
    }

    @Test
    void extractClaim_ShouldReturnClaimValue() {
        String expectedClaimValue = "testUser";

        Function<Claims, String> claimsResolver = Claims::getSubject;
        Claims claims = mock(Claims.class);
        when(claimsResolver.apply(claims)).thenReturn(expectedClaimValue);

        String extractedClaimValue = jwtService.extractClaim(token, claimsResolver);

        assertEquals(expectedClaimValue, extractedClaimValue);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsNotExpired() {
        Date actualExpiration = jwtService.extractExpiration(token);

        assertFalse(actualExpiration.before(new Date()));
    }
}
