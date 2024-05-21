package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the CustomUserDetailsService interface providing custom user details retrieval functionality.
 * This service class retrieves user details based on the user code provided and is used for authentication purposes.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(CustomUserDetailsServiceImpl.class);

    @Override
    public UserDetailsService userDetailsService() {
        return userCode -> {
            logger.trace("Loading user details for user code: {}", userCode);

            // Retrieve user details from the repository based on the user code
            return userRepository.findByUserCode(userCode)
                    .orElseThrow(() -> {
                        logger.error("User not found for user code: {}", userCode);
                        return new UserNotFoundException("User not found");
                    });
        };
    }
}
