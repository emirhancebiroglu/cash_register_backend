package com.bit.usermanagementservice.dto.kafka;

import com.bit.usermanagementservice.entity.Role;
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
