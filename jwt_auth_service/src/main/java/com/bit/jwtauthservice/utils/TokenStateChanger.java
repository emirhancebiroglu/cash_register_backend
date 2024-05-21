package com.bit.jwtauthservice.utils;

import com.bit.jwtauthservice.entity.Token;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenStateChanger {
    private final TokenRepository tokenRepository;
    private static final Logger logger = LogManager.getLogger(TokenStateChanger.class);

    /**
     * Saves the JWT token for the user in the database.
     *
     * @param user     The user for whom the token is generated.
     * @param jwtToken The JWT token to save.
     */
    public void saveUserToken(User user, String jwtToken) {
        logger.trace("Saving JWT token for user {} in the database", user.getUsername());

        var token = Token.builder()
                .user(user)
                .jwtToken(jwtToken)
                .revoked(false)
                .expired(false).build();

        tokenRepository.save(token);
        logger.trace("Saved JWT token for user {} in the database", user.getUsername());
    }

    /**
     * Revokes all tokens associated with the user in the database.
     *
     * @param user The user for whom to revoke tokens.
     */
    public void revokeAllUserTokens(User user){
        logger.trace("Revoking all tokens for user {}", user.getUsername());

        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());

        if (validUserTokens.isEmpty()) {
            logger.trace("No valid tokens found for user {}", user.getUsername());
            return;
        }

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
        logger.trace("Revoked all tokens for user {}", user.getUsername());
    }
}
