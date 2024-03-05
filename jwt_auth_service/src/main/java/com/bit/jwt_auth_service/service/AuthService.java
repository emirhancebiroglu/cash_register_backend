package com.bit.jwt_auth_service.service;

import com.bit.jwt_auth_service.dto.Login.LoginReq;
import com.bit.jwt_auth_service.dto.Login.LoginRes;
import com.bit.jwt_auth_service.dto.TokenValidationReq;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    LoginRes login(@RequestBody LoginReq loginReq);
    boolean validateToken(@RequestBody TokenValidationReq tokenValidationReq);
    String extractUsername(@RequestBody TokenValidationReq tokenValidationReq);
}
