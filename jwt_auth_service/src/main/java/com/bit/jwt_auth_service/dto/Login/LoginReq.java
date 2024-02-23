package com.bit.jwt_auth_service.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {
    @NonNull
    private String userCode;
    @NonNull
    private String password;
}
