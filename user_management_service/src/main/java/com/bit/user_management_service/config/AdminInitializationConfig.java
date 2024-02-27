package com.bit.user_management_service.config;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@AllArgsConstructor
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

    public void initializeAdmin() throws RoleNotFoundException{
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN not found"));

        if (isAdminInitializationRequired(adminRole)) {
            createAdminUser(adminRole);
        }
        else{
            logger.info("There is already a user with admin role. Skipping default admin initialization.");
        }
    }

    private boolean isAdminInitializationRequired(Role adminRole) {
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        List<User> usersWithAdminRole = userRepository.findByRoles(roles);

        return usersWithAdminRole.isEmpty() || usersWithAdminRole.stream().allMatch(User::isDeleted);
    }
    private void createAdminUser(Role adminRole) {
        User adminUser = User.builder()
                .firstName("admin")
                .lastName("admin")
                .email("admin@gmail.com")
                .userCode("admin")
                .password(passwordEncoderConfig.passwordEncoder().encode("admin"))
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(adminUser);
        logger.info("There is no user with admin role. Default admin user is initialized");
    }
}