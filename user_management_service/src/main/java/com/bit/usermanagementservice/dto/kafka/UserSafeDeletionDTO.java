package com.bit.usermanagementservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
