package com.bit.jwt_auth_service.dto;

import com.bit.jwt_auth_service.entity.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserCredentialsDTO {
    private String userCode;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private boolean isDeleted;
}
