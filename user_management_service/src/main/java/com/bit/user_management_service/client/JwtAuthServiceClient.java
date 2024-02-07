package com.bit.user_management_service.client;

import com.bit.shared.dto.TokenValidationReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${jwt.service-name}", url = "${jwt.auth-service-url}")
@Component
public interface JwtAuthServiceClient {
  @PostMapping("/validate-token")
  boolean validateToken(@RequestBody TokenValidationReq tokenValidationReq);

  @PostMapping("/extract-username")
  String extractUsername(@RequestBody TokenValidationReq tokenValidationReq);
}
