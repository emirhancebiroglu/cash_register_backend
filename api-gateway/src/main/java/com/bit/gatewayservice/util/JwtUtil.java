package com.bit.gatewayservice.util;

import com.bit.gatewayservice.exceptions.missingauthorizationheader.MissingAuthorizationHeaderException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret-key}")
  String jwtSecretKey;

  /**
   * Validates the given JWT token.
   *
   * @param token JWT token to be validated
   */
  public void validateToken(final String token) {
    Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token);
  }


  /**
   * Retrieves the signing key from the JWT secret key.
   *
   * @return Key object representing the signing key
   */
  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Extracts authorities (roles) from the given JWT token.
   *
   * @param token JWT token from which authorities are extracted
   * @return List of authorities extracted from the token
   */
  public List<String> extractAuthorities(String token) {
    Key key = getSignKey();

    Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);

    Claims claims = claimsJws.getBody();

    @SuppressWarnings("unchecked")
    List<String> authorities = claims.get("authorities", List.class);

    return authorities;
  }


  /**
   * Checks if the given request has any of the specified roles.
   *
   * @param request ServerHttpRequest object representing the request
   * @param roles   List of roles to check for
   * @return true if the request has any of the specified roles, false otherwise
   * @throws MissingAuthorizationHeaderException if authorization header is missing in the request
   */
  public boolean hasAnyRole(ServerHttpRequest request, List<String> roles) {
    String token = extractToken(request);
    List<String> authorities = extractAuthorities(token);

    for (String role : roles) {
      if (authorities.contains(role)) {
        return true;
      }
    }

    return false;
  }


  /**
   * Extracts JWT token from the given ServerHttpRequest.
   *
   * @param request ServerHttpRequest object from which token is extracted
   * @return JWT token extracted from the request
   * @throws MissingAuthorizationHeaderException if authorization header is missing in the request
   */
  private String extractToken(ServerHttpRequest request) {
    List<String> authorizationHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

    if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
      String authorizationHeader = authorizationHeaders.get(0);

      if (authorizationHeader.startsWith("Bearer ")) {
        return authorizationHeader.substring(7);
      }
    }

    throw new MissingAuthorizationHeaderException("Missing authorization header");
  }

  public boolean isRefreshToken(String token) {
    Claims claims = extractAllClaims(token);

    String tokenType = claims.get("tokenType", String.class);
    return tokenType != null && tokenType.equals("refresh");
  }

  public Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

}
