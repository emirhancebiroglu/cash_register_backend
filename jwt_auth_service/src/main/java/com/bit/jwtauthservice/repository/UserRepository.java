package com.bit.jwtauthservice.repository;

import com.bit.jwtauthservice.entity.ResetPasswordToken;
import com.bit.jwtauthservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user.
     * @return An optional containing the user, or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a user by their user code.
     *
     * @param userCode The user code of the user.
     * @return An optional containing the user, or empty if not found.
     */
    Optional<User> findByUserCode(String userCode);

    /**
     * Retrieves a user by their reset password token.
     *
     * @param resetPasswordToken The reset password token associated with the user.
     * @return An optional containing the user, or empty if not found.
     */
    Optional<User> findByResetPasswordToken(ResetPasswordToken resetPasswordToken);
}
