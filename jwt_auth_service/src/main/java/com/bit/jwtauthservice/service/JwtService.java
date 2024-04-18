package com.bit.jwtauthservice.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {
  /**
   * Retrieves the signing key used for generating JWTs.
   *
   * @return The signing key.
   */
  Key getSigningKey();

  /**
   * Generates a JWT token for the specified user details.
   *
   * @param userDetails The user details.
   * @return The generated JWT token.
   */
  String generateToken(UserDetails userDetails);

  /**
   * Generates a refresh token for the specified user details.
   *
   * @param userDetails The user details.
   * @return The generated refresh token.
   */
  String generateRefreshToken(UserDetails userDetails);

  /**
   * Extracts the username from the JWT token.
   *
   * @param token The JWT token.
   * @return The extracted username.
   */
  String extractUsername(String token);

  /**
   * Extracts a specific claim from the JWT token using the provided resolver function.
   *
   * @param token          The JWT token.
   * @param claimsResolver The resolver function to extract the claim.
   * @param <T>            The type of the claim.
   * @return The extracted claim.
   */
  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  /**
   * Checks if the JWT token is valid for the specified user details.
   *
   * @param token       The JWT token.
   * @param userDetails The user details.
   * @return True if the token is valid, false otherwise.
   */
  boolean isTokenValid(String token, UserDetails userDetails);

  /**
   * Checks if the JWT token is expired.
   *
   * @param token The JWT token.
   * @return True if the token is expired, false otherwise.
   */
  boolean isTokenExpired(String token);

  /**
   * Extracts the expiration date from the JWT token.
   *
   * @param token The JWT token.
   * @return The expiration date.
   */
  Date extractExpiration(String token);

  /**
   * Extracts all claims from the JWT token.
   *
   * @param token The JWT token.
   * @return All claims extracted from the token.
   */
  Claims extractAllClaims(String token);
}
