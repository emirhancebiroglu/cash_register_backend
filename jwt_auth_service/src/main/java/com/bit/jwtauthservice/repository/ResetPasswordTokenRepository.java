package com.bit.jwtauthservice.repository;

import com.bit.jwtauthservice.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    /**
     * Retrieves a reset password token by its token value.
     *
     * @param token The token value.
     * @return An optional containing the reset password token, or empty if not found.
     */
    Optional<ResetPasswordToken> findByToken(String token);
}
