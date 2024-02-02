package com.bit.user_management_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(schema = "users", name = "_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String name;
}
