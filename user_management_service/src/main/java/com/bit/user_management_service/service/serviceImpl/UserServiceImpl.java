package com.bit.user_management_service.service.serviceImpl;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.dto.UserDTO;
import com.bit.user_management_service.exceptions.RoleNotFoundException;
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
        boolean isAdminRoleExist = roleRepository.findByName("ROLE_ADMIN").isPresent();
        boolean isInitialAdminExist = userRepository.findByEmail("admin@gmail.com").isPresent();

        Set<Role> roles = userDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        User newUser = User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(passwordEncoderConfig.passwordEncoder().encode(userDTO.getPassword()))
                .roles(roles)
                .build();

        if(isAdminRoleExist  & roles.contains(roleRepository.findByName("ROLE_ADMIN").get()) & isInitialAdminExist){
            userRepository.delete(userRepository.findByEmail("admin@gmail.com").get());
        }
        userRepository.save(newUser);
    }

    @Override
    public void updateUser(Long user_id, UserDTO userDTO) throws Exception{
        User existingUser = userRepository.findById(user_id).orElseThrow(() -> new Exception("User not found"));

        if (existingUser != null){
            Set<Role> roles = userDTO.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName)))
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
