package com.bit.jwtauthservice.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService {
    /**
     * Returns a UserDetailsService instance.
     *
     * @return An instance of UserDetailsService.
     */
    UserDetailsService userDetailsService();
}
