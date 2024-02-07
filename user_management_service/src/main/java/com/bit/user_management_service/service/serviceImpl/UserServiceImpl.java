package com.bit.user_management_service.service.serviceImpl;

import com.bit.shared.config.PasswordEncoderConfig;
import com.bit.shared.entity.Role;
import com.bit.shared.entity.User;
import com.bit.shared.repository.RoleRepository;
import com.bit.shared.repository.UserRepository;
import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Override
    public void addUser(UserDTO userDTO) {
        boolean isAdminRoleExist = roleRepository.findByName("ADMIN").isPresent();
        boolean isInitialAdminExist = userRepository.findByEmail("admin@gmail.com").isPresent();

        Set<Role> roles = userDTO.getRoles().stream()
                .map(role_name -> roleRepository.findByName(role_name)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role_name)))
                .collect(Collectors.toSet());

        User newUser = User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(passwordEncoderConfig.passwordEncoder().encode(userDTO.getPassword()))
                .roles(roles)
                .build();

        if(isAdminRoleExist && roles.contains(roleRepository.findByName("ADMIN").get()) && isInitialAdminExist){
            userRepository.delete(userRepository.findByEmail("admin@gmail.com").get());
        }
        userRepository.save(newUser);
    }

    @Override
    public void updateUser(Long user_id, UserDTO userDTO) {
        User existingUser = userRepository.findById(user_id).orElse(null);

        if (existingUser != null){
            Set<Role> roles = userDTO.getRoles().stream()
                    .map(role_name -> roleRepository.findByName(role_name).orElseThrow(() -> new RuntimeException("Role not found: " + role_name)))
                    .collect(Collectors.toSet());

            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setPassword(passwordEncoderConfig.passwordEncoder().encode(userDTO.getPassword()));
            existingUser.setRoles(roles);

            userRepository.save(existingUser);
        }
    }

    @Override
    public void deleteUser(Long user_id) throws Exception{
        User existingUser = userRepository.findById(user_id).orElseThrow(() -> new Exception("User not found"));
        existingUser.setDeleted(true);
        userRepository.save(existingUser);
    }
}
