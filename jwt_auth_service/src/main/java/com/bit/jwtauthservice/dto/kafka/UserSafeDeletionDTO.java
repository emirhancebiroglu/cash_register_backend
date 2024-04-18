package com.bit.jwtauthservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for safely deleting user used in Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
