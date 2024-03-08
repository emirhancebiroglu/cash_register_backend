package com.bit.sharedfilter.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.util.Collection;
import java.util.function.Function;

public interface JwtServiceForFiltering {
    String extractUserName(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolvers);
    Claims extractAllClaims(String token);
    Key getSigningKey();
    Collection<GrantedAuthority> extractAuthorities(String token);
}
