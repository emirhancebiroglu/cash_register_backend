package com.bit.user_management_service.config;

import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.repository.RoleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@Transactional
@Order(1)
public class RoleInitializationConfig implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleInitializationConfig.class);

    @Override
    public void run(String... args){
        initializeRoles();
    }

    protected void initializeRoles() {
        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_CASHIER", "ROLE_STORE_MANAGER");

        for(String roleName : roleNames){
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