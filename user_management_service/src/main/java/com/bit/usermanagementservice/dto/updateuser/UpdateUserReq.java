package com.bit.usermanagementservice.dto.updateuser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserReq {
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
