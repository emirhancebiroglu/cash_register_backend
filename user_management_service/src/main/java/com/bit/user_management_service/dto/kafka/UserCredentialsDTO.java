package com.bit.user_management_service.dto.kafka;

import com.bit.user_management_service.entity.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserCredentialsDTO {
    private Long id;
    private String userCode;
    private String password;
    private Set<Role> roles = new HashSet<>();
    boolean isDeleted = false;
}
