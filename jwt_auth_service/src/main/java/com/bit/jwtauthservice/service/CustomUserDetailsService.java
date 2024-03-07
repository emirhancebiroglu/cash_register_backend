package com.bit.jwtauthservice.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService {
    UserDetailsService userDetailsService();
}
