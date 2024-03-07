package com.bit.usermanagementservice.dto.kafka;

import com.bit.usermanagementservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserCredentialsDTO {
    private Long id;
    private String email;
    private String userCode;
    private String password;
    private Set<Role> roles;
    boolean isDeleted;
}
