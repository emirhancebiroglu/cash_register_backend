package com.bit.user_management_service.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String extractUserName(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolvers);
    Date extractExpiration(String token);
    Claims extractAllClaims(String token);
    Key getSigningKey();

}
