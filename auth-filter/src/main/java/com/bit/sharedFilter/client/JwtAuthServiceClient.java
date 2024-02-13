package com.bit.sharedFilter.client;


import com.bit.sharedClasses.dto.TokenValidationReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "localhost", url = "http://localhost:8881/api/auth")
@Component
public interface JwtAuthServiceClient {
    @PostMapping("/validate-token")
    boolean validateToken(@RequestBody TokenValidationReq tokenValidationReq);

    @PostMapping("/extract-username")
    String extractUsername(@RequestBody TokenValidationReq tokenValidationReq);
}

