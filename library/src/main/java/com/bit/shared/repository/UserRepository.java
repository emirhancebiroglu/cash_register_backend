package com.bit.shared.repository;

import com.bit.shared.entity.Role;
import com.bit.shared.entity.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>{
   Optional<User> findByEmail(String username);

   @NonNull
   Optional<User> findById(@NonNull Long userId);

   @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
   List<User> findByRoles(Collection<Role> roles);
}