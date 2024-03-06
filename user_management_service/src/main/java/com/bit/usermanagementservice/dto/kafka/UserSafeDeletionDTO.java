package com.bit.usermanagementservice.dto.kafka;

import lombok.Data;

@Data
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
