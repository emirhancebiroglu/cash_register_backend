package com.bit.sharedClasses.service.serviceImpl;

import com.bit.sharedClasses.repository.UserRepository;
import com.bit.sharedClasses.service.CustomUserDetailsService;
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
