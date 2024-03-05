package com.bit.jwt_auth_service.dto.kafka;

import com.bit.jwt_auth_service.entity.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserUpdateDTO {
    private Long id;
    private String userCode;
    private Set<Role> roles = new HashSet<>();
}
