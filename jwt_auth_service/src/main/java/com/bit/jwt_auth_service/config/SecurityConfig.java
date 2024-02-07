package com.bit.jwt_auth_service.config;

import com.bit.jwt_auth_service.service.service_impl.CustomUserDetailsService;
import com.bit.shared.config.PasswordEncoderConfig;
import lombok.RequiredArgsConstructor;
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


  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth
            -> auth.requestMatchers("api/auth/login").permitAll()
                    .requestMatchers("api/auth/extract-username").permitAll()
                    .requestMatchers("api/auth/validate-token").permitAll()
                   .anyRequest()
                   .authenticated())

        .build();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider =
        new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(customUserDetailsService.userDetailsService());
    authenticationProvider.setPasswordEncoder(
        passwordEncoderConfig.passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  AuthenticationManager
  authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
