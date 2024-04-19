package com.bit.usermanagementservice.repository;

import com.bit.usermanagementservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The RoleRepository interface provides CRUD operations for Role entities.
 * It extends JpaRepository to inherit basic database operations and querying capabilities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    /**
     * Finds a role by its name.
     *
     * @param roleName the name of the role to find.
     * @return an Optional containing the role if found, otherwise an empty Optional.
     */
    Optional<Role> findByName(String roleName);
}
