package com.bit.usermanagementservice.dto.kafka;

import com.bit.usermanagementservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserCredentialsDTO {
    private final Long id;
    private final String userCode;
    private final String password;
    private final Set<Role> roles;
    boolean isDeleted;
}
