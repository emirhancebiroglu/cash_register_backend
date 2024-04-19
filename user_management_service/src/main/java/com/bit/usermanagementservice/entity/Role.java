package com.bit.usermanagementservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a role.
 */
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

  /**
   * Constructor with role name parameter.
   * @param roleName The name of the role.
   */
  public Role(String roleName){
    name = roleName;
  }
}
