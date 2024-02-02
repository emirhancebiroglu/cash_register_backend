package com.bit.user_management_service.repository;

import com.bit.user_management_service.entity.Role;
import com.bit.user_management_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
    Optional<User> findByRoles(Collection<Role> roles);
}
