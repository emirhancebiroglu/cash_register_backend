package com.bit.jwtauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a request to refresh an access token.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenReq {
    private String refreshToken;
}
