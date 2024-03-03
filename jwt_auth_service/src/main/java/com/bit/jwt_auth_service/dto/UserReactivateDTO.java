package com.bit.jwt_auth_service.dto;

import lombok.Data;

@Data
public class UserReactivateDTO {
    private Long id;
    private String password;
    private boolean isDeleted;
}
