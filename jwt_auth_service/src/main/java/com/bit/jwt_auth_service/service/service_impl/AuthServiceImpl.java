package com.bit.jwt_auth_service.service.service_impl;

import com.bit.jwt_auth_service.dto.Login.LoginReq;
import com.bit.jwt_auth_service.dto.Login.LoginRes;
import com.bit.jwt_auth_service.dto.TokenValidationReq;
import com.bit.jwt_auth_service.dto.kafka.UserDetailsDTO;
import com.bit.jwt_auth_service.entity.Role;
import com.bit.jwt_auth_service.entity.User;
import com.bit.jwt_auth_service.repository.UserRepository;
import com.bit.jwt_auth_service.service.AuthService;
import com.bit.jwt_auth_service.service.JwtService;
import com.bit.jwt_auth_service.utils.UserDetailsProducer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@Data
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsProducer userDetailsProducer;

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public LoginRes login(LoginReq loginReq) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginReq.getUserCode(),
                            loginReq.getPassword()));
            User user = userRepository.findByUserCode(loginReq.getUserCode())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            sendUserDetailsToAuthFilterModule(user.getUserCode(), user.getPassword(), user.getRoles());

            var jwt = jwtService.generateToken(user);

            return LoginRes.builder().token(jwt).build();
        }
        catch (BadCredentialsException ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", ex);
        }
    }

    @Override
    public boolean validateToken(TokenValidationReq tokenValidationReq) {
        try {
            String token = tokenValidationReq.getToken();
            String userCode = tokenValidationReq.getUserCode();
            return jwtService.isTokenValid(token, userCode);
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

    private void sendUserDetailsToAuthFilterModule(String userCode, String password, Set<Role> roles){
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUserCode(userCode);
        userDetailsDTO.setPassword(password);
        userDetailsDTO.setRoles(roles);

        userDetailsProducer.sendMessage("user-details", userDetailsDTO);
    }
}
