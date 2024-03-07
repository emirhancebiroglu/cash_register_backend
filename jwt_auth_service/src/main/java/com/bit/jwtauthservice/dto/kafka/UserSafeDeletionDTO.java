package com.bit.jwtauthservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSafeDeletionDTO {
    private Long id;
    private boolean isDeleted;
}
