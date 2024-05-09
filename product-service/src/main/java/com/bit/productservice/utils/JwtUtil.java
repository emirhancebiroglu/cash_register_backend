package com.bit.productservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 */
@Component
public class JwtUtil {

  @Value("${jwt.secret-key}")
  String jwtSecretKey;

  /**
   * Extracts the user id from the JWT token.
   *
   * @param token the JWT token
   * @return the extracted user id
   */
  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  /**
   * Extracts a claim from the JWT token using the given claims resolver.
   *
   * @param token          the JWT token
   * @param claimsResolver the claims resolver function
   * @param <T>            the type of the claim
   * @return the extracted claim
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from the JWT token.
   *
   * @param token the JWT token
   * @return all claims extracted from the token
   */
  protected Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  /**
   * Retrieves the signing key for JWT token verification.
   *
   * @return the signing key
   */
  private Key getSignKey() {
    byte[] keyBytes= Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
