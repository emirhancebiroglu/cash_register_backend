package com.bit.gatewayservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret-key}")
  String jwtSecretKey;

  public void validateToken(final String token) {
    Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token);
  }

  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
