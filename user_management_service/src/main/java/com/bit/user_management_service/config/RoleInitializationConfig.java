package com.bit.user_management_service.config;

import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@Transactional
@Order(1)
public class RoleInitializationConfig implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args){
        initializeRoles();
    }

    protected void initializeRoles() {
        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_CASHIER", "ROLE_STORE-MANAGER");

        for(String role_name : roleNames){
            if(roleRepository.findByName(role_name).isEmpty()){
                Role role = new Role();
                role.setName(role_name);
                roleRepository.save(role);
            }
        }
    }
}
