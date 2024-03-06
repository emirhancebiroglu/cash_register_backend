package com.bit.usermanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "users", name = "_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
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
  @Builder.Default
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "is_deleted")
  private boolean isDeleted;
}
