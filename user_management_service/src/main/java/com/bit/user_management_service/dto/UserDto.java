package com.bit.user_management_service.dto;

import com.bit.user_management_service.annotations.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String userCode;
    @ValidPassword
    private String password;
    @NotEmpty
    private Set<String> roles;
}
