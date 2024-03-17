package com.bit.productservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Getter
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    JwtUtil jwtUtil;

    private final String jwtSecret = "ea8419c9befbc532a8219250e11192e6bbd693e5a5c76c60135d8551ee6ee059";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "jwtSecretKey", jwtSecret);

    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        byte[] keyBytes= Decoders.BASE64.decode(jwtSecret);
        String token = Jwts
                .builder()
                .setClaims(null)
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60000000))
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
                .compact();

        assertEquals("testUser", jwtUtil.extractUsername(token));
    }
}