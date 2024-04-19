package com.bit.usermanagementservice.dto.adduser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

/**
 * Request DTO class for adding a new user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddUserReq {
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String email;
    @NonNull
    private Set<String> roles;
}
