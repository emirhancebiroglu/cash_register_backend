package com.bit.usermanagementservice.dto.kafka;

import com.bit.usermanagementservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * DTO class for updating user information via Kafka.
 */
@Getter
@AllArgsConstructor
public class UserUpdateDTO {
    private Long id;
    private String email;
    private String userCode;
    private Set<Role> roles;
}
