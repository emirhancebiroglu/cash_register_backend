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

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
   Optional<User> findByEmail(String email);
   Optional<User> findByUserCode(String userCode);
   @Query(value = "SELECT MAX(id) FROM User")
   Long findMaxId();
   @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
   List<User> findByRoles(Collection<Role> roles);
   Page<User> findByisDeletedFalse(Pageable pageable);
   Page<User> findByisDeletedTrue(Pageable pageable);
   Page<User> findByFirstNameStartingWithIgnoreCaseAndLastNameStartingWithIgnoreCase(String firstNamePrefix, String lastNamePrefix, Pageable pageable);
   Page<User> findByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCase(String firstNamePrefix, String lastNamePrefix, Pageable pageable);

}