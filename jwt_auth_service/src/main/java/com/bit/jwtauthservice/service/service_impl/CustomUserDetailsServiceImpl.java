package com.bit.jwtauthservice.service.service_impl;

import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import com.bit.jwtauthservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    @Override
    public UserDetailsService userDetailsService() {
        return userCode -> {
            logger.info("Loading user details for user code: {}", userCode);

            return userRepository.findByUserCode(userCode)
                    .orElseThrow(() -> {
                        logger.error("User not found for user code: {}", userCode);
                        return new UserNotFoundException("User not found");
                    });
        };
    }
}
