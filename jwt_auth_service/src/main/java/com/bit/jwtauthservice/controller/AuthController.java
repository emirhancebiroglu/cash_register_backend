  package com.bit.jwtauthservice.controller;

  import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
  import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
  import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
  import com.bit.jwtauthservice.dto.login.LoginReq;
  import com.bit.jwtauthservice.dto.login.LoginRes;
  import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
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
    public ResponseEntity<String> forgotUserCode(@RequestBody ForgotUserCodeReq forgotUserCodeReq){
      authService.forgotUserCode(forgotUserCodeReq);
      return ResponseEntity.status(HttpStatus.OK).body("User code sent successfully!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordReq forgotPasswordReq){
      authService.forgotPassword(forgotPasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password reset email sent successfully!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody ResetPasswordReq resetPasswordReq){
      authService.resetPassword(token, resetPasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordReq changePasswordReq){
      authService.changePassword(changePasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }
  }