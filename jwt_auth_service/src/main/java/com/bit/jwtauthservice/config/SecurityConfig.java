package com.bit.jwtauthservice.config;

import com.bit.jwtauthservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final PasswordEncoderConfig passwordEncoderConfig;
  private final CustomUserDetailsService customUserDetailsService;
  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    logger.info("Configuring security filter chain...");

    SecurityFilterChain filterChain = http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                    auth -> auth.requestMatchers("api/auth/login").permitAll()
                            .requestMatchers("api/auth/forgot-user-code").permitAll()
                            .anyRequest()
                            .authenticated())
            .build();

    logger.info("Security filter chain configured successfully.");

    return filterChain;
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    logger.info("Creating authentication provider...");

    DaoAuthenticationProvider authenticationProvider =
        new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(customUserDetailsService.userDetailsService());
    authenticationProvider.setPasswordEncoder(
        passwordEncoderConfig.passwordEncoder());

    logger.info("Authentication provider created successfully.");

    return authenticationProvider;
  }

  @Bean
  AuthenticationManager
  authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    logger.info("Getting authentication manager...");

    AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

    logger.info("Authentication manager retrieved successfully.");

    return authenticationManager;
  }
}
