package com.bit.jwt_auth_service.service;

import com.bit.jwt_auth_service.dto.JwtAuthResponse;
import com.bit.jwt_auth_service.dto.LoginReq;
import com.bit.sharedClasses.dto.TokenValidationReq;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    JwtAuthResponse authenticateAndGetToken(@RequestBody LoginReq loginReq);
    boolean validateToken(@RequestBody TokenValidationReq tokenValidationReq);
    String extractUsername(@RequestBody TokenValidationReq tokenValidationReq);
}
