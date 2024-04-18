package com.bit.jwtauthservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a refresh token entity used for generating new access tokens.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "_refreshtoken", schema = "user-credentials")
public class RefreshToken {
    /**
     * The unique identifier of the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The refresh token string.
     */
    private String token;

    /**
     * The expiry date of the refresh token.
     */
    private Instant expiryDate;

    /**
     * The user associated with the refresh token.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
