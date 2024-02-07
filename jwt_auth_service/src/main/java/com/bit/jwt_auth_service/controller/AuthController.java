  package com.bit.jwt_auth_service.controller;

  import com.bit.jwt_auth_service.dto.JwtAuthResponse;
  import com.bit.jwt_auth_service.dto.LoginRequest;
  import com.bit.jwt_auth_service.service.JwtService;
  import com.bit.jwt_auth_service.service.service_impl.CustomUserDetailsService;
  import com.bit.shared.dto.TokenValidationReq;
  import com.bit.shared.entity.User;
  import com.bit.shared.repository.UserRepository;
  import lombok.*;
  import org.springframework.security.authentication.AuthenticationManager;
  import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
  import org.springframework.security.core.userdetails.UserDetails;
  import org.springframework.web.bind.annotation.*;

  @RestController
  @RequestMapping("api/auth")
  @Data
  public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    public JwtAuthResponse
    authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                                  loginRequest.getPassword()));
      User user = userRepository.findByEmail(loginRequest.getEmail())
              .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
      var jwt = jwtService.generateToken(user);

      return JwtAuthResponse.builder().token(jwt).build();
    }

    @PostMapping("/validate-token")
    public boolean validateToken(@RequestBody TokenValidationReq tokenValidationReq){
      String token = tokenValidationReq.getToken();
      String username = tokenValidationReq.getUsername();
      return jwtService.isTokenValid(token, username);
    }

    @PostMapping("/extract-username")
    public String extractUsername(@RequestBody TokenValidationReq tokenValidationReq){
      String token = tokenValidationReq.getToken();
      return jwtService.extractUserName(token);
    }
  }