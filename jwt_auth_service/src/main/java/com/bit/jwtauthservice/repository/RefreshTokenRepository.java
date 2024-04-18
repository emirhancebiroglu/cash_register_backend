package com.bit.jwtauthservice.repository;

import com.bit.jwtauthservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String tokenValue);
    RefreshToken findByUserId(Long id);
}
