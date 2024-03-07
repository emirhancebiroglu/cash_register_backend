  package com.bit.jwtauthservice.controller;

  import com.bit.jwtauthservice.dto.login.LoginReq;
  import com.bit.jwtauthservice.dto.login.LoginRes;
  import com.bit.jwtauthservice.service.AuthService;
  import lombok.Data;
  import org.springframework.http.HttpStatus;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.*;

  @RestController
  @RequestMapping("api/auth")
  @Data
  public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq loginReq){
      return authService.login(loginReq);
    }

    @PostMapping("/forgot-user-code")
    public ResponseEntity<String> forgotUserCode(@RequestParam String email){
      authService.forgotUserCode(email);
      return ResponseEntity.status(HttpStatus.OK).body("User code sent successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email){
      authService.forgotPassword(email);
      return ResponseEntity.status(HttpStatus.OK).body("Password reset email sent successfully!");
    }
  }