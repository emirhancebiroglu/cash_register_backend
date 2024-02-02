package com.bit.user_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class UserDTO {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private Set<String> roles;
}
