package com.bit.jwt_auth_service.dto.kafka;

import lombok.Data;

@Data
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
