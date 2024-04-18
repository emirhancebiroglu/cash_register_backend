package com.bit.jwtauthservice.repository;

import com.bit.jwtauthservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    /**
     * Retrieves all valid tokens associated with a user.
     *
     * @param userId The ID of the user.
     * @return A list of valid tokens associated with the user.
     */
    @Query("""
    select t from Token t inner join User u on t.user.id = u.id
    where u.id = :userId and (t.expired = false or t.revoked = false)
""")
    List<Token> findAllValidTokensByUser(Long userId);

    /**
     * Retrieves a token by its JWT token string.
     *
     * @param token The JWT token string.
     * @return An optional containing the token, or empty if not found.
     */
    Optional<Token> findByJwtToken(String token);
}
