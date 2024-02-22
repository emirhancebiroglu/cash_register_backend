package com.bit.jwt_auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NonNull
    private String userCode;
    @NonNull
    private String password;
}
