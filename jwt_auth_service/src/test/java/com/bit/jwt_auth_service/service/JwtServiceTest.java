package com.bit.jwt_auth_service.service;


import com.bit.jwt_auth_service.service.service_impl.JwtServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", "ea8419c9befbc532a8219250e11192e6bbd693e5a5c76c60135d8551ee6ee059");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
        userDetails = new User("testUser",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

        token = jwtService.generateToken(userDetails);
    }

    @Test
    void extractUserName_ValidToken_ShouldExtractUserName() {
        String username = jwtService.extractUserName(token);

        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_ValidTokenAndUsername_ShouldReturnTrue() {
        boolean isValid = jwtService.isTokenValid(token, userDetails.getUsername());

        assertTrue(isValid);
    }

    @Test
    void isTokenExpired_ValidToken_ShouldReturnFalse() {
        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void extractExpiration_ValidToken_ShouldExtractExpirationDate() {
        Date expirationDate = jwtService.extractExpiration(token);

        assertNotNull(expirationDate);
    }

    @Test
    void extractAllClaims_ValidToken_ShouldExtractAllClaims() {
        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
    }

    @Test
    void generateToken_WithUserDetails_ShouldGenerateToken() {
        assertNotNull(token);
    }
}
