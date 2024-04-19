package com.bit.usermanagementservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO class for reactivating a user via Kafka.
 */
@Getter
@AllArgsConstructor
public class UserReactivateDTO {
    private Long id;
    private String password;
    private boolean isDeleted;
}
