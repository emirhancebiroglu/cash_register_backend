package com.bit.jwtauthservice.utils;

import com.bit.jwtauthservice.entity.Token;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenStateChanger {
    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(TokenStateChanger.class);

    /**
     * Saves the JWT token for the user in the database.
     *
     * @param user     The user for whom the token is generated.
     * @param jwtToken The JWT token to save.
     */
    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .jwtToken(jwtToken)
                .revoked(false)
                .expired(false).build();

        tokenRepository.save(token);
        logger.info("Saved JWT token for user {} in the database", user.getUsername());
    }

    /**
     * Revokes all tokens associated with the user in the database.
     *
     * @param user The user for whom to revoke tokens.
     */
    public void revokeAllUserTokens(User user){
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());

        if (validUserTokens.isEmpty()) {
            logger.info("No valid tokens found for user {}", user.getUsername());
            return;
        }

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
        logger.info("Revoked all tokens for user {}", user.getUsername());
    }
}
