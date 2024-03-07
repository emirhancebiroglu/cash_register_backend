package com.bit.usermanagementservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReactivateDTO {
    private Long id;
    private String password;
    private boolean isDeleted;
}
