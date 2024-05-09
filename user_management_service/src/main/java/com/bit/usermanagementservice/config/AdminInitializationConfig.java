package com.bit.usermanagementservice.config;

import com.bit.usermanagementservice.dto.kafka.UserCredentialsDTO;
import com.bit.usermanagementservice.dto.kafka.UserSafeDeletionDTO;
import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.entity.User;
import com.bit.usermanagementservice.exceptions.rolenotfound.RoleNotFoundException;
import com.bit.usermanagementservice.repository.RoleRepository;
import com.bit.usermanagementservice.repository.UserRepository;
import com.bit.usermanagementservice.utils.CredentialsProducer;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Configuration class for initializing the admin user.
 */
@Configuration
@RequiredArgsConstructor
@Order(2)
public class AdminInitializationConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final CredentialsProducer credentialsProducer;
    private static final Logger logger = LogManager.getLogger(AdminInitializationConfig.class);
    private static final String ADMIN_STRING = "admin";

    /**
     * Runs the initialization process when the application starts.
     */
    @Override
    public void run(String... args){
        initializeAdmin();
    }

    /**
     * Initializes the admin user if necessary.
     *
     * @throws RoleNotFoundException if the admin role is not found in the repository.
     */
    public void initializeAdmin() throws RoleNotFoundException{
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> {
                    logger.error("ROLE_ADMIN not found");
                    return new RoleNotFoundException("ROLE_ADMIN not found");
                });

        if (isAdminInitializationRequired(adminRole)) {
            createAdminUser(adminRole);
        } else {
            logger.info("There is already a user with admin role. Skipping default admin initialization.");
        }
    }

    /**
     * Checks if admin initialization is required based on the presence of an admin user in the repository.
     *
     * @param adminRole the admin role to check against.
     * @return true if admin initialization is required, false otherwise.
     */
    private boolean isAdminInitializationRequired(Role adminRole) {
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        List<User> usersWithAdminRole = userRepository.findByRoles(roles);

        return usersWithAdminRole.isEmpty() || usersWithAdminRole.stream().allMatch(User::isDeleted);
    }

    /**
     * Creates the admin user if it does not already exist.
     *
     * @param adminRole the admin role to assign to the admin user.
     */
    private void createAdminUser(Role adminRole) {
        Optional<User> initialAdmin = userRepository.findByUserCode(ADMIN_STRING);

        if (initialAdmin.isEmpty()){
            User adminUser = new User(
                    ADMIN_STRING,
                    ADMIN_STRING,
                    "admin@gmail.com",
                    ADMIN_STRING,
                    passwordEncoderConfig.passwordEncoder().encode(ADMIN_STRING),
                    Set.of(adminRole)
            );

            userRepository.save(adminUser);

            sendCredentialsToAuthService(adminUser);
            logger.info("There is no user with admin role. Default admin user is initialized");
        }
        else{
            initialAdmin.get().setDeleted(false);

            sendDeletionInfoToAuthService(initialAdmin);

            userRepository.save(initialAdmin.get());
            logger.info("Default admin user is reactivated");
        }
    }

    /**
     * Sends the admin user's credentials to the authentication service.
     *
     * @param adminUser the admin user whose credentials are to be sent.
     */
    private void sendCredentialsToAuthService(User adminUser) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(
                adminUser.getId(),
                adminUser.getEmail(),
                adminUser.getUserCode(),
                adminUser.getPassword(),
                adminUser.getRoles(),
                adminUser.isDeleted()
        );

        credentialsProducer.sendMessage("user-credentials" ,userCredentialsDTO);
        logger.info("User credentials sent to the authentication service.");
    }

    /**
     * Sends the deletion information of the admin user to the authentication service.
     *
     * @param initialAdmin an optional containing the admin user's information.
     */
    private void sendDeletionInfoToAuthService(Optional<User> initialAdmin){
        if (initialAdmin.isEmpty())
            return;

        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO(
                initialAdmin.get().getId(),
                initialAdmin.get().isDeleted()
        );

        credentialsProducer.sendMessage("user-deletion", userSafeDeletionDTO);
        logger.info("User deletion information sent to the authentication service.");
    }
}