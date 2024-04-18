package com.bit.jwtauthservice.repository;

import com.bit.jwtauthservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Retrieves a role by its name.
     *
     * @param roleName The name of the role.
     * @return An optional containing the role, or empty if not found.
     */
    Optional<Role> findByName(String roleName);
}
