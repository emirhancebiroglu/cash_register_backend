  package com.bit.jwt_auth_service.controller;

  import com.bit.jwt_auth_service.dto.Login.LoginReq;
  import com.bit.jwt_auth_service.dto.Login.LoginRes;
  import com.bit.jwt_auth_service.service.AuthService;
  import lombok.Data;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;

  @RestController
  @RequestMapping("api/auth")
  @Data
  public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq loginReq){
      return authService.login(loginReq);
    }
  }