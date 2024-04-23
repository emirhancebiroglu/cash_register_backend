package com.bit.usermanagementservice.repository;

import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The UserRepository interface provides CRUD operations for User entities.
 * It extends JpaRepository to inherit basic database operations and querying capabilities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
   /**
    * Finds a user by their email.
    *
    * @param email the email of the user to find.
    * @return an Optional containing the user if found, otherwise an empty Optional.
    */
   Optional<User> findByEmail(String email);

   /**
    * Finds a user by their user code.
    *
    * @param userCode the user code of the user to find.
    * @return an Optional containing the user if found, otherwise an empty Optional.
    */
   Optional<User> findByUserCode(String userCode);

   /**
    * Finds the maximum ID of users.
    *
    * @return the maximum ID of users.
    */
   @Query(value = "SELECT MAX(id) FROM User")
   Long findMaxId();

   /**
    * Finds users by their roles.
    *
    * @param roles the roles to search for.
    * @return a list of users with the specified roles.
    */
   @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
   List<User> findByRoles(Collection<Role> roles);

    Page<User> findAllByisDeletedAndFirstNameContainingIgnoreCase(boolean deleted, String searchingTerm, Pageable pageable);

   Page<User> findAllByisDeleted(boolean deleted, Pageable pageable);

   Page<User> findAllByFirstNameContainingIgnoreCase(String searchingTerm, Pageable pageable);
}