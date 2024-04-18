package com.bit.jwtauthservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for reactivating user used in Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReactivateDTO {
    private Long id;
    private String password;
    private boolean isDeleted;
}
