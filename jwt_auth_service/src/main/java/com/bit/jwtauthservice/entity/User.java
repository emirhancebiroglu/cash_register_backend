package com.bit.jwtauthservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity class representing a user.
 */
@Entity
@Table(schema = "user-credentials", name = "_users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "tokens")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    Long id;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reset_password_token_id", referencedColumnName = "id")
    private ResetPasswordToken resetPasswordToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Token> tokens;

    public User(Long id, @NonNull String email, @NonNull String userCode, @NonNull String password,
                @NonNull Set<Role> roles, boolean isDeleted){
        this.id = id;
        this.email = email;
        this.userCode = userCode;
        this.password = password;
        this.roles = roles;
        this.isDeleted = isDeleted;
    }

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
