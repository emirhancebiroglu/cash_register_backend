package com.bit.jwtauthservice.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for handling login responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {
    private String accessToken;
    private String refreshToken;
}
