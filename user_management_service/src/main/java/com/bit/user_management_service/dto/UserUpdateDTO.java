package com.bit.user_management_service.dto;

import com.bit.sharedClasses.entity.Role;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserUpdateDTO {
    private Long id;
    private String userCode;
    private Set<Role> roles = new HashSet<>();
}
