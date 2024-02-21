package com.bit.sharedClasses.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(schema = "users", name = "_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {
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

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());
  }

  @Override
  public String getUsername() {
    return userCode;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
