package com.bit.user_management_service.dto.UpdateUser;

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
public class UpdateUserReq {
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;
}
