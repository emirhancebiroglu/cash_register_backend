package com.bit.usermanagementservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
  @NonNull
  private String firstName;

  @Column(name = "last_name", nullable = false)
  @NonNull
  private String lastName;

  @Column(name = "email", nullable = false, unique = true)
  @NonNull
  private String email;

  @Column(name = "user_code", unique = true, nullable = false)
  @NonNull
  private String userCode;

  @Column(name = "password", nullable = false)
  @NonNull
  private String password;

  @NonNull
  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "is_deleted")
  private boolean isDeleted;

  public User(@NonNull String firstName, @NonNull String lastName, @NonNull String email,
              @NonNull String userCode, @NonNull String password, @NonNull Set<Role> roles){
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.userCode = userCode;
    this.password = password;
    this.roles = roles;
  }
}
