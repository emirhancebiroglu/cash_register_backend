package com.bit.jwt_auth_service.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Map;

public interface JwtService {
  Key getSigningKey();
  String generateToken(UserDetails userDetails);
  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
}
