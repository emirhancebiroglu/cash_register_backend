package com.bit.jwt_auth_service.config;

import com.bit.jwt_auth_service.entity.Role;
import com.bit.jwt_auth_service.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@Transactional
@Order(1)
public class RoleInitializationConfig implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleInitializationConfig.class);

    private static final List<String> ROLE_NAMES = Arrays.asList("ROLE_ADMIN", "ROLE_CASHIER", "ROLE_STORE_MANAGER");

    @Override
    public void run(String... args){
        initializeRoles();
    }

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