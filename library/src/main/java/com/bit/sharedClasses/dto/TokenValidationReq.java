package com.bit.sharedClasses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenValidationReq {
    String token;
    String userCode;

    public TokenValidationReq(String token){
        this.token = token;
    }
}
