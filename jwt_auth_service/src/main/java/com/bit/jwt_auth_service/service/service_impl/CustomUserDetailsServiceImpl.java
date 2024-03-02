package com.bit.jwt_auth_service.service.service_impl;

import com.bit.jwt_auth_service.repository.UserRepository;
import com.bit.jwt_auth_service.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return userCode -> userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
