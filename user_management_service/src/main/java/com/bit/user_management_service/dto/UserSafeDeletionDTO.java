package com.bit.user_management_service.dto;

import lombok.Data;

@Data
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
