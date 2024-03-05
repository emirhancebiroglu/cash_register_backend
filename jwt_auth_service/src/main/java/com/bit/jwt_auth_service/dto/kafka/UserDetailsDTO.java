package com.bit.jwt_auth_service.dto.kafka;

import com.bit.jwt_auth_service.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDetailsDTO {
    private String userCode;
    private String password;
    private Set<Role> roles = new HashSet<>();
}
