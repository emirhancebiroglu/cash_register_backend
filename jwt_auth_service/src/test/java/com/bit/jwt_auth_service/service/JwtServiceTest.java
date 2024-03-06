package com.bit.jwt_auth_service.service;


import com.bit.jwt_auth_service.service.service_impl.JwtServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void generateToken_WithUserDetails_ShouldGenerateToken() {
        assertNotNull(token);
    }
}
