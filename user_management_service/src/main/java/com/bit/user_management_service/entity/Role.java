package com.bit.user_management_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(schema = "users", name = "_roles")
@NoArgsConstructor
public class Role{
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
