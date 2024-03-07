package com.bit.jwtauthservice.service;

import com.bit.jwtauthservice.dto.login.LoginReq;
import com.bit.jwtauthservice.dto.login.LoginRes;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    LoginRes login(@RequestBody LoginReq loginReq);
    void forgotUserCode(String email);
    void forgotPassword(String email);
}
