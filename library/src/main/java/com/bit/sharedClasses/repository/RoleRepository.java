package com.bit.sharedClasses.repository;

import com.bit.sharedClasses.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByName(String roleName);
}
