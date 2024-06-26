package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

/**
 * Implementation of the JwtService interface providing JSON Web Token (JWT) functionality.
 * This service class is responsible for generating tokens, extracting claims, and validating tokens.
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
  private final UserRepository userRepository;

  @Value("${jwt.secret-key}")
  private String jwtSecretKey;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @Value("${jwt.refresh-token.expiration}")
  private Long refreshExpiration;

  private static final Logger logger = LogManager.getLogger(JwtServiceImpl.class);

  @Override
  public Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    logger.trace("Generating token for user: {}", userDetails.getUsername());

    String userCode = userDetails.getUsername();
    User user = userRepository.findByUserCode(userCode)
            .orElseThrow(() -> {
              logger.error("Could not find user for user code {}", userCode);
              return new UserNotFoundException("User not found");
            });

    // Claims include user authorities and user ID
    Map<String, Object> claims = Map.ofEntries(
            entry("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()),
            entry("userId", user.getId())
    );

    String token = buildToken(claims, userDetails, jwtExpiration);

    logger.trace("Token generated successfully for user: {}", userDetails.getUsername());

    return token;
  }

  @Override
  public String generateRefreshToken(UserDetails userDetails) {
    logger.trace("Generating refresh token for user: {}", userDetails.getUsername());

    // Claims include authorities and token type
    Map<String, Object> claims = Map.ofEntries(
            entry("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()),
            entry("tokenType", "refresh")
    );

    String token = buildToken(claims, userDetails, refreshExpiration);

    logger.trace("Token generated successfully for user: {}", userDetails.getUsername());

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

  // Helper method to build the token
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
