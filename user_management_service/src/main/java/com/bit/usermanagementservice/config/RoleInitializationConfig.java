package com.bit.usermanagementservice.config;

import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.repository.RoleRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for initializing roles.
 */
@Configuration
@AllArgsConstructor
@Transactional
@Order(1)
public class RoleInitializationConfig implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private static final Logger logger = LogManager.getLogger(RoleInitializationConfig.class);

    private static final List<String> ROLE_NAMES = Arrays.asList("ROLE_ADMIN", "ROLE_CASHIER", "ROLE_STORE_MANAGER");

    /**
     * Runs the initialization process for roles.
     */
    @Override
    public void run(String... args){
        initializeRoles();
    }

    /**
     * Initializes roles if they do not already exist.
     */
    protected void initializeRoles() {
        for(String roleName : ROLE_NAMES){
            if(roleRepository.findByName(roleName).isEmpty()){
                Role role = new Role(roleName);
                roleRepository.save(role);
                logger.info("Role '{}' initialized successfully.", roleName);
            }
            else{
                logger.info("Role '{}' already exists. Skipping initialization.", roleName);
            }
        }
    }
}