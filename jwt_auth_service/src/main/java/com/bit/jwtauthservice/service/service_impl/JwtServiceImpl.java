package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.service.JwtService;
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

import static java.util.Map.entry;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.secret-key}")
  String jwtSecretKey;

  @Value("${jwt.expiration}")
  Long jwtExpirationMs;

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
    String token = generateToken(claims, userDetails);

    logger.info("Token generated successfully for user: {}", userDetails.getUsername());

    return token;
  }

  @Override
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }
}
