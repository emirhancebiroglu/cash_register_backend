package com.bit.user_management_service.dto;

import lombok.Data;

@Data
public class UserReactivateDTO {
    private Long id;
    private String password;
    private boolean isDeleted;
}
