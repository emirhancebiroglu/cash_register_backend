package com.bit.jwt_auth_service.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
  String extractUserName(String token);
  boolean isTokenValid(String token, String username);
  boolean isTokenExpired(String token);
  <T> T extractClaim(String token, Function<Claims, T> claimsResolvers);
  Date extractExpiration(String token);
  Claims extractAllClaims(String token);
  Key getSigningKey();
  String generateToken(UserDetails userDetails);
  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

}
