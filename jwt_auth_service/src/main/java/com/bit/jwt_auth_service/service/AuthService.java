package com.bit.jwt_auth_service.service;

import com.bit.jwt_auth_service.dto.Login.LoginReq;
import com.bit.jwt_auth_service.dto.Login.LoginRes;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    LoginRes login(@RequestBody LoginReq loginReq);
}
