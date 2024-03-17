package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.secret-key}")
  private String jwtSecretKey;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @Value("${jwt.refresh-token.expiration}")
  private Long refreshExpiration;

  private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

  @Override
  public Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    logger.info("Generating token for user: {}", userDetails.getUsername());

    Map<String, Object> claims = Map.ofEntries(
            entry("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList())
    );

    String token = buildToken(claims, userDetails, jwtExpiration);

    logger.info("Token generated successfully for user: {}", userDetails.getUsername());

    return token;
  }

  @Override
  public String generateRefreshToken(UserDetails userDetails) {
    logger.info("Generating refresh token for user: {}", userDetails.getUsername());

    Map<String, Object> claims = Map.ofEntries(
            entry("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList())
    );

    String token = buildToken(claims, userDetails, refreshExpiration);

    logger.info("Token generated successfully for user: {}", userDetails.getUsername());

    return token;
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  @Override
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  @Override
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  @Override
  public Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long expiration){
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }
}
