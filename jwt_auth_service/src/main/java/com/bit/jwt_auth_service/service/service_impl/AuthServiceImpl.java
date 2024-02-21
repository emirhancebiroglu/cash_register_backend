package com.bit.jwt_auth_service.service.service_impl;

import com.bit.jwt_auth_service.dto.JwtAuthResponse;
import com.bit.jwt_auth_service.dto.LoginRequest;
import com.bit.jwt_auth_service.service.AuthService;
import com.bit.jwt_auth_service.service.JwtService;
import com.bit.sharedClasses.dto.TokenValidationReq;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public JwtAuthResponse authenticateAndGetToken(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserCode(),
                            loginRequest.getPassword()));
            User user = userRepository.findByUserCode(loginRequest.getUserCode())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            var jwt = jwtService.generateToken(user);

            return JwtAuthResponse.builder().token(jwt).build();
        }
        catch (BadCredentialsException ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", ex);
        }
    }

    @Override
    public boolean validateToken(TokenValidationReq tokenValidationReq) {
        try {
            String token = tokenValidationReq.getToken();
            String username = tokenValidationReq.getUsername();
            return jwtService.isTokenValid(token, username);
        }
        catch (Exception ex){
            logger.info(ex.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating token", ex);
        }
    }

    @Override
    public String extractUsername(TokenValidationReq tokenValidationReq) {
        try {
            String token = tokenValidationReq.getToken();
            return jwtService.extractUserName(token);
        }
        catch (Exception ex){
            logger.info(ex.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error extracting username from token", ex);
        }
    }
}
