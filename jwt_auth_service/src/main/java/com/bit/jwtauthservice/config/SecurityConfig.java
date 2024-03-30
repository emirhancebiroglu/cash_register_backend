package com.bit.jwtauthservice.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final LogoutHandler logoutHandler;
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    logger.info("Configuring security filter chain...");

    SecurityFilterChain filterChain = http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                    auth -> auth.anyRequest().permitAll())
            .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext())))
            .build();

    logger.info("Security filter chain configured successfully.");

    return filterChain;
  }
}
