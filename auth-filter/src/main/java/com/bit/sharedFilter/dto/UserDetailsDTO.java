package com.bit.sharedFilter.dto;

import com.bit.sharedFilter.model.Role;
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
