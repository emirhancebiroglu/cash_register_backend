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

    @Override
    public RefreshToken createRefreshToken(User user) {
        logger.trace("Creating a new refresh token for user: {}", user.getUserCode());

        if (userRepository.findByUserCode(user.getUserCode()).isPresent()){
            // Create a new refresh token
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(userRepository.findByUserCode(user.getUserCode()).get())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)) // 7 days expiry
                    .build();

            logger.trace("Created refresh token for user {}", user.getUsername());
            return refreshTokenRepository.save(refreshToken);
        }

        logger.error("Couldn't find user for user code {}", user.getUserCode());
        throw new UserNotFoundException("User not found");
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        logger.trace("Verifying if the refresh token has expired for user {}", token.getUser().getUsername());

        // Check if token has expired
        if (token.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            logger.warn("Refresh token expired for user {}", token.getUser().getUsername());
            throw new ExpiredRefreshTokenException("Refresh token has expired. Please make a new sign-in request.");
        }
        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
