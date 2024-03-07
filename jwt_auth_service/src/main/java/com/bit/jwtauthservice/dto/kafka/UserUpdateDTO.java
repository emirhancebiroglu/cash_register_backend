package com.bit.jwtauthservice.dto.kafka;

import com.bit.jwtauthservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private Long id;
    private String email;
    private String userCode;
    private Set<Role> roles;
}
