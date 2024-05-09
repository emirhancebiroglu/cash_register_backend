package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.exceptions.tokennotfound.TokenNotFoundException;
import com.bit.jwtauthservice.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Implementation of the LogoutHandler interface responsible for handling logout requests.
 * This service class revokes the JWT token stored in the database upon logout.
 */
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private static final Logger logger = LogManager.getLogger(LogoutServiceImpl.class);

    /**
     * Handles logout requests by revoking the JWT token stored in the database and clearing the security context.
     *
     * @param request        The HTTP servlet request.
     * @param response       The HTTP servlet response.
     * @param authentication The authentication object representing the current user's authentication.
     */
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        logger.info("Initiating logout process.");

        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);

        var storedToken = tokenRepository.findByJwtToken(jwt)
                .orElseThrow(() -> {
                    logger.error("Could not find token in tokenRepository for JWT token {}", jwt);
                    return new TokenNotFoundException("Token not found");
                });

        logger.info("Found token in repository.");

        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
        SecurityContextHolder.clearContext();

        logger.info("Logout process completed successfully.");
    }
}
