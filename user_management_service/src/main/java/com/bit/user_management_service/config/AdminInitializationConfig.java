package com.bit.user_management_service.config;

import com.bit.user_management_service.dto.kafka.UserCredentialsDTO;
import com.bit.user_management_service.dto.kafka.UserSafeDeletionDTO;
import com.bit.user_management_service.entity.Role;
import com.bit.user_management_service.entity.User;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import com.bit.user_management_service.repository.RoleRepository;
import com.bit.user_management_service.repository.UserRepository;
import com.bit.user_management_service.utils.CredentialsProducer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Data
@Order(2)
public class AdminInitializationConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final CredentialsProducer credentialsProducer;

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
        Optional<User> initialAdmin = userRepository.findByUserCode("admin");

        if (initialAdmin.isEmpty()){
            User adminUser = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@gmail.com")
                    .userCode("admin")
                    .password(passwordEncoderConfig.passwordEncoder().encode("admin"))
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(adminUser);

            sendCredentialsToAuthService(adminUser);
        }
        else{
            initialAdmin.get().setDeleted(false);

            sendDeletionInfoToAuthService(initialAdmin);

            userRepository.save(initialAdmin.get());
        }

        logger.info("There is no user with admin role. Default admin user is initialized");
    }

    private void sendCredentialsToAuthService(User adminUser) {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setId(adminUser.getId());
        userCredentialsDTO.setUserCode(adminUser.getUserCode());
        userCredentialsDTO.setPassword(adminUser.getPassword());
        userCredentialsDTO.setRoles(adminUser.getRoles());

        credentialsProducer.sendMessage("user-credentials" ,userCredentialsDTO);
    }
    private void sendDeletionInfoToAuthService(Optional<User> initialAdmin){
        if (initialAdmin.isEmpty())
            return;

        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO();
        userSafeDeletionDTO.setId(initialAdmin.get().getId());
        userSafeDeletionDTO.setDeleted(initialAdmin.get().isDeleted());

        credentialsProducer.sendMessage("user-deletion", userSafeDeletionDTO);
    }
}