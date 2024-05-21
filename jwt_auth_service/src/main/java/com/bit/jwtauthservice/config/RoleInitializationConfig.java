package com.bit.jwtauthservice.config;

import com.bit.jwtauthservice.entity.Role;
import com.bit.jwtauthservice.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

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
     * Method to run the role initialization process.
     * @param args Command line arguments
     */
    @Override
    public void run(String... args){
        initializeRoles();
    }

    /**
     * Method to initialize roles.
     */
    protected void initializeRoles() {
        for(String roleName : ROLE_NAMES){
            if(roleRepository.findByName(roleName).isEmpty()){
                Role role = new Role(roleName);
                roleRepository.save(role);
                logger.trace("Role '{}' initialized successfully.", roleName);
            }
            else{
                logger.trace("Role '{}' already exists. Skipping initialization.", roleName);
            }
        }
    }
}