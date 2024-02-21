package com.bit.user_management_service.service.serviceImpl;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.dto.UserDto;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import com.bit.user_management_service.exceptions.UserAlreadyExists.UserAlreadyExistsException;
import com.bit.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Override
    public void addUser(UserDto userDto) {
        if (!handleExistingUserForAddUser(userDto)){
            return;
        }

        Set<Role> roles = userDto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        User newUser = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .userCode(userDto.getUserCode())
                .password(passwordEncoderConfig.passwordEncoder().encode(userDto.getPassword()))
                .roles(roles)
                .build();

        handleInitialAdminForAddUser(roles);

        userRepository.save(newUser);
    }

    @Override
    public void updateUser(Long user_id, UserDto UserDto) throws Exception{
        User existingUser = userRepository.findById(user_id).orElseThrow(() -> new Exception("User not found"));

        if (existingUser != null){
            Set<Role> roles = UserDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());

            existingUser.setFirstName(UserDto.getFirstName());
            existingUser.setLastName(UserDto.getLastName());
            existingUser.setUserCode(UserDto.getUserCode());
            existingUser.setPassword(passwordEncoderConfig.passwordEncoder().encode(UserDto.getPassword()));
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

    private boolean handleExistingUserForAddUser(UserDto userDto){
        Optional<User> existingUser = userRepository.findByUserCode(userDto.getUserCode());

        if (existingUser.isPresent() && !existingUser.get().isDeleted()){
            throw new UserAlreadyExistsException("User already exists: " + existingUser.get().getUserCode());
        }
        else {
            if (existingUser.isPresent()){
                existingUser.get().setDeleted(false);
                userRepository.save(existingUser.get());
                return false;
            }
        }
        return true;
    }

    private void handleInitialAdminForAddUser(Set<Role> roles){
        Optional<User> initialAdmin = userRepository.findByUserCode("admin@gmail.com");
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");

        if (adminRole.isPresent()){
            if (roles.contains(adminRole.get()) & initialAdmin.isPresent()){
                userRepository.delete(initialAdmin.get());
            }
        }
    }
}
