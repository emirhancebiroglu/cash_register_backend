package com.bit.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenValidationReq {
    String token;
    String username;

    public TokenValidationReq(String token){
        this.token = token;
    }
}
