package com.bit.usermanagementservice.dto.getuser;

import com.bit.usermanagementservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDTO {
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String email;
    @NonNull
    private Set<Role> roles;
}
