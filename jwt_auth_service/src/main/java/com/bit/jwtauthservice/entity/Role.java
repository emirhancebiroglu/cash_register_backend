package com.bit.jwtauthservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user role.
 */
@Entity
@Data
@NoArgsConstructor
@Table(schema = "user-credentials", name = "_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "id")
    private Long id;

    @Column(name = "role_name")
    private String name;

    public Role(String roleName){
        name = roleName;
    }
}
