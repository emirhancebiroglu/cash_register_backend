package com.bit.usermanagementservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO class for safe deletion of a user via Kafka.
 */
@Getter
@AllArgsConstructor
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
