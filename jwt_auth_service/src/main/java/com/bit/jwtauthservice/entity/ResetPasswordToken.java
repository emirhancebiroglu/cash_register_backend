package com.bit.jwtauthservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing a reset password token.
 */
@Entity
@Table(name = "_resetpasswordtoken", schema = "user-credentials")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String token;
    LocalDate expirationDate;
}
