package com.bit.user_management_service.config;

import com.bit.shared.entity.Role;
import com.bit.shared.entity.User;
import com.bit.shared.repository.RoleRepository;
import com.bit.shared.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        initializeAdmin();
    }

    private void initializeAdmin() {
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);

        if (adminRole != null){
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            List<User> usersWithAdminRole = userRepository.findByRoles(roles);

            if (usersWithAdminRole.isEmpty()){
                User user = User.builder()
                    .firstName("admin")
                    .lastName("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin"))
                    .roles(roles)
                    .build();

                userRepository.save(user);
            }
        }
    }
}