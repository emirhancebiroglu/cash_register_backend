package com.bit.user_management_service.config;

import com.bit.user_management_service.entity.Role;
import com.bit.user_management_service.entity.User;
import com.bit.user_management_service.repository.RoleRepository;
import com.bit.user_management_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
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
        if (roleRepository.findByName("ADMIN").isPresent()){
            Role adminRole = roleRepository.findByName("ADMIN").get();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            if (userRepository.findByRoles(roles).isEmpty()){
                User user = User.builder()
                    .first_name("admin")
                    .last_name("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin"))
                    .roles(roles)
                    .build();

                userRepository.save(user);
            }
        }
    }
}