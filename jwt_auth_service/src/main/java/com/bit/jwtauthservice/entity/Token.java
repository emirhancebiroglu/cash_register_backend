package com.bit.jwtauthservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "jwt-token", schema = "user-credentials")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "token", unique = true, nullable = false)
    private String jwtToken;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
