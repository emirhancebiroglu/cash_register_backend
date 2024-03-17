package com.bit.productservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.function.Function;

@Component
public class JwtUtil {

  @Value("${jwt.secret-key}")
  String jwtSecretKey;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  protected Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private Key getSignKey() {
    byte[] keyBytes= Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
