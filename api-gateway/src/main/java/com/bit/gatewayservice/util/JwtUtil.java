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

  public List<String> extractAuthorities(String token) {
    Key key = getSignKey();

    Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);

    Claims claims = claimsJws.getBody();
    return claims.get("authorities", List.class);
  }

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
}
