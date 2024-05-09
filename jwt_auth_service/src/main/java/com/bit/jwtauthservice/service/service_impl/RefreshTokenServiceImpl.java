package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.entity.RefreshToken;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.expiredrefreshtoken.ExpiredRefreshTokenException;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.RefreshTokenRepository;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the RefreshTokenService interface providing operations related to refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(RefreshTokenServiceImpl.class);

    /**
     * Creates a new refresh token for the specified user.
     *
     * @param user the user for whom the refresh token is created.
     * @return the created refresh token.
     * @throws UserNotFoundException if the user is not found in the repository.
     */
    @Override
    public RefreshToken createRefreshToken(User user) {
        logger.info("Creating a new refresh token for user: {}", user.getUserCode());

        if (userRepository.findByUserCode(user.getUserCode()).isPresent()){
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(userRepository.findByUserCode(user.getUserCode()).get())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                    .build();

            logger.info("Created refresh token for user {}", user.getUsername());
            return refreshTokenRepository.save(refreshToken);
        }

        logger.error("Couldn't find user for user code {}", user.getUserCode());
        throw new UserNotFoundException("User not found");
    }

    /**
     * Verifies if the given refresh token has expired.
     *
     * @param token the refresh token to verify.
     * @return the refresh token if it has not expired.
     * @throws ExpiredRefreshTokenException if the refresh token has expired.
     */
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        logger.info("Verifying if the refresh token has expired for user {}", token.getUser().getUsername());

        if (token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            logger.warn("Refresh token expired for user {}", token.getUser().getUsername());
            throw new ExpiredRefreshTokenException("Refresh token has expired. Please make a new sign-in request.");
        }
        return token;
    }

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the token string to search for.
     * @return an optional containing the refresh token if found, empty otherwise.
     */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
