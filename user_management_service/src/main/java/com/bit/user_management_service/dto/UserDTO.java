package com.bit.user_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<String> roles;
}
