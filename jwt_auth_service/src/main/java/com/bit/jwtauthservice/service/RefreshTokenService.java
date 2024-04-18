package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.entity.RefreshToken;
import com.bit.jwtauthservice.entity.User;

import java.util.Optional;

/**
 * Service interface for managing refresh tokens.
 */
public interface RefreshTokenService {
    /**
     * Creates a new refresh token for the specified user.
     *
     * @param user the user for whom the refresh token is created.
     * @return the created refresh token.
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Verifies if the given refresh token has expired.
     *
     * @param token the refresh token to verify.
     * @return the refresh token if it has not expired.
     */
    RefreshToken verifyExpiration(RefreshToken token);

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for.
     * @return an optional containing the refresh token if found, empty otherwise.
     */
    Optional<RefreshToken> findByToken(String token);
}
