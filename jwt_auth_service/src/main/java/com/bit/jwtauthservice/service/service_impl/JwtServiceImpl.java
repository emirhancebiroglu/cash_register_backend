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

  /**
   * Retrieves the signing key for JWT.
   *
   * @return The signing key.
   */
  @Override
  public Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Generates a JWT token for the given user details.
   *
   * @param userDetails The user details for whom the token is generated.
   * @return The generated JWT token.
   */
  @Override
  public String generateToken(UserDetails userDetails) {
    logger.info("Generating token for user: {}", userDetails.getUsername());

    String userCode = userDetails.getUsername();
    User user = userRepository.findByUserCode(userCode)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    Map<String, Object> claims = Map.ofEntries(
            entry("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()),
            entry("userId", user.getId())
    );

    String token = buildToken(claims, userDetails, jwtExpiration);

    logger.info("Token generated successfully for user: {}", userDetails.getUsername());

    return token;
  }

  /**
   * Generates a refresh token for the given user details.
   *
   * @param userDetails The user details for whom the refresh token is generated.
   * @return The generated refresh token.
   */
  @Override
  public String generateRefreshToken(UserDetails userDetails) {
    logger.info("Generating refresh token for user: {}", userDetails.getUsername());

    Map<String, Object> claims = Map.ofEntries(
            entry("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()),
            entry("tokenType", "refresh")
    );

    String token = buildToken(claims, userDetails, refreshExpiration);

    logger.info("Token generated successfully for user: {}", userDetails.getUsername());

    return token;
  }

  /**
   * Extracts the username from the JWT token.
   *
   * @param token The JWT token from which the username is extracted.
   * @return The extracted username.
   */
  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from the JWT token.
   *
   * @param token           The JWT token from which the claim is extracted.
   * @param claimsResolver  A function to resolve the claim from the token's claims.
   * @param <T>             The type of the extracted claim.
   * @return The extracted claim.
   */
  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Checks if the JWT token is valid for the given user details.
   *
   * @param token         The JWT token to be validated.
   * @param userDetails   The user details against which the token is validated.
   * @return True if the token is valid, otherwise false.
   */
  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * Checks if the JWT token has expired.
   *
   * @param token The JWT token to be checked for expiration.
   * @return True if the token has expired, otherwise false.
   */
  @Override
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration date of the JWT token.
   *
   * @param token The JWT token from which the expiration date is extracted.
   * @return The expiration date of the token.
   */
  @Override
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts all claims from the JWT token.
   *
   * @param token The JWT token from which all claims are extracted.
   * @return The claims extracted from the token.
   */
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
