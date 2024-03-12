package com.bit.usermanagementservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "users", name = "_users")
@NoArgsConstructor
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  Long id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "user_code", unique = true, nullable = false)
  private String userCode;

  @Column(name = "password", nullable = false)
  private String password;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "is_deleted")
  private boolean isDeleted;

  public User(String firstName, String lastName, String email,
              String userCode, String password, Set<Role> roles){
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.userCode = userCode;
    this.password = password;
    this.roles = roles;
  }
}
