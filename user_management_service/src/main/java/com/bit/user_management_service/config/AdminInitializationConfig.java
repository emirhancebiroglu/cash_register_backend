package com.bit.user_management_service.config;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.exceptions.RoleNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@AllArgsConstructor
@Transactional
@Order(2)
public class AdminInitializationConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializationConfig.class);

    @Override
    public void run(String... args){
        initializeAdmin();
    }

    protected void initializeAdmin() {
        try {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            List<User> usersWithAdminRole = userRepository.findByRoles(roles);

            if (usersWithAdminRole.isEmpty()){
                User adminUser = User.builder()
                        .firstName("admin")
                        .lastName("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoderConfig.passwordEncoder().encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(adminUser);
                logger.info("Admin user initialized successfully.");
            }
            else{
                logger.info("Admin user already exists. Skipping initialization.");
            }
        }
        catch (Exception e){
            logger.error("An error occurred while initializing admin user: {}", e.getMessage());
        }
    }
}