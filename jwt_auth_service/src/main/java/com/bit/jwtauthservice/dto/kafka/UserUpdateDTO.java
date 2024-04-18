package com.bit.jwtauthservice.dto.kafka;

import com.bit.jwtauthservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data transfer object for updating user information used in Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    private Long id;
    private String email;
    private String userCode;
    private Set<Role> roles;
}
