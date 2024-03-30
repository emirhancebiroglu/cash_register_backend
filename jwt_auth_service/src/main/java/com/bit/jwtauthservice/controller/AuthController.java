  package com.bit.jwtauthservice.controller;

  import com.bit.jwtauthservice.dto.login.LoginReq;
  import com.bit.jwtauthservice.dto.login.LoginRes;
  import com.bit.jwtauthservice.dto.password.ChangePasswordReq;
  import com.bit.jwtauthservice.dto.password.ForgotPasswordReq;
  import com.bit.jwtauthservice.dto.password.ResetPasswordReq;
  import com.bit.jwtauthservice.dto.usercode.ForgotUserCodeReq;
  import com.bit.jwtauthservice.service.AuthService;
  import jakarta.servlet.http.HttpServletRequest;
  import jakarta.servlet.http.HttpServletResponse;
  import jakarta.validation.Valid;
  import lombok.RequiredArgsConstructor;
  import org.springframework.http.HttpStatus;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.*;
  import reactor.core.publisher.Mono;

  import java.io.IOException;

  @RestController
  @RequestMapping("/api/auth")
  @RequiredArgsConstructor
  public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq loginReq){
      return authService.login(loginReq);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
      authService.refreshToken(request, response);
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
    public ResponseEntity<String> resetPassword(@RequestParam String token,@Valid @RequestBody ResetPasswordReq resetPasswordReq){
      authService.resetPassword(token, resetPasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordReq changePasswordReq){
      authService.changePassword(changePasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }

    @GetMapping("/validate-token")
    public Mono<Boolean> validateToken(@RequestParam String jwt){
      return authService.validateToken(jwt);
    }
  }