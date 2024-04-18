package com.bit.jwtauthservice.dto.kafka;

import com.bit.jwtauthservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data transfer object for user credentials used in Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDTO {
    private Long id;
    private String email;
    private String userCode;
    private String password;
    private Set<Role> roles;
    boolean isDeleted;
}
