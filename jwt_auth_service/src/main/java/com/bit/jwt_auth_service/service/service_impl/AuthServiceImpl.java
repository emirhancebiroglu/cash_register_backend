package com.bit.jwt_auth_service.service.service_impl;

import com.bit.jwt_auth_service.dto.Login.LoginReq;
import com.bit.jwt_auth_service.dto.Login.LoginRes;
import com.bit.jwt_auth_service.entity.User;
import com.bit.jwt_auth_service.repository.UserRepository;
import com.bit.jwt_auth_service.service.AuthService;
import com.bit.jwt_auth_service.service.JwtService;
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

@Service
@Data
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public LoginRes login(LoginReq loginReq) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginReq.getUserCode(),
                            loginReq.getPassword()));
            User user = userRepository.findByUserCode(loginReq.getUserCode())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            var jwt = jwtService.generateToken(user);

            return LoginRes.builder().token(jwt).build();
        }
        catch (BadCredentialsException ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials", ex);
        }
    }
}
