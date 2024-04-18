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

  /**
   * Controller class for handling authentication-related endpoints.
   */
  @RestController
  @RequestMapping("/api/auth")
  @RequiredArgsConstructor
  public class AuthController {
    private final AuthService authService;

    /**
     * Endpoint for user login.
     * @param loginReq Login request object
     * @return Login response object
     */
    @PostMapping("/login")
    public LoginRes login(@RequestBody LoginReq loginReq){
      return authService.login(loginReq);
    }

    /**
     * Endpoint for refreshing authentication token.
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
      authService.refreshToken(request, response);
    }

    /**
     * Endpoint for requesting a user code.
     * @param forgotUserCodeReq Forgot user code request object
     * @return ResponseEntity indicating the success status
     */
    @PostMapping("/forgot-user-code")
    public ResponseEntity<String> forgotUserCode(@RequestBody ForgotUserCodeReq forgotUserCodeReq){
      authService.forgotUserCode(forgotUserCodeReq);
      return ResponseEntity.status(HttpStatus.OK).body("User code sent successfully!");
    }

    /**
     * Endpoint for requesting a password reset email.
     * @param forgotPasswordReq Forgot password request object
     * @return ResponseEntity indicating the success status
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordReq forgotPasswordReq){
      authService.forgotPassword(forgotPasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password reset email sent successfully!");
    }

    /**
     * Endpoint for resetting a password.
     * @param token Token for password reset
     * @param resetPasswordReq Reset password request object
     * @return ResponseEntity indicating the success status
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,@Valid @RequestBody ResetPasswordReq resetPasswordReq){
      authService.resetPassword(token, resetPasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully!");
    }

    /**
     * Endpoint for changing a password.
     * @param changePasswordReq Change password request object
     * @return ResponseEntity indicating the success status
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordReq changePasswordReq){
      authService.changePassword(changePasswordReq);
      return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");
    }

    /**
     * Endpoint for validating an authentication token.
     * @param jwt Authentication token
     * @return Mono indicating if the token is valid
     */
    @GetMapping("/validate-token")
    public Mono<Boolean> validateToken(@RequestParam String jwt){
      return authService.validateToken(jwt);
    }
  }