package com.bit.jwtauthservice.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {
  Key getSigningKey();
  String generateToken(UserDetails userDetails);
  String generateRefreshToken(UserDetails userDetails);
  String extractUsername(String token);
  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
  boolean isTokenValid(String token, UserDetails userDetails);
  boolean isTokenExpired(String token);
  Date extractExpiration(String token);
  Claims extractAllClaims(String token);
}
