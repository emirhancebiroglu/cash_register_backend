package com.bit.sharedfilter.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String extractUserName(String token);
    boolean isTokenValid(String token);
    boolean isTokenExpired(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolvers);
    Date extractExpiration(String token);
    Claims extractAllClaims(String token);
    Key getSigningKey();
    Collection<GrantedAuthority> extractAuthorities(String token);
}
