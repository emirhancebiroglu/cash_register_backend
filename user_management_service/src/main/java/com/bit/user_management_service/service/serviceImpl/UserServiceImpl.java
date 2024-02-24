package com.bit.user_management_service.service.serviceImpl;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.dto.AddUser.AddUserReq;
import com.bit.user_management_service.dto.UpdateUser.UpdateUserReq;
import com.bit.user_management_service.exceptions.InvalidEmail.InvalidEmailException;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import com.bit.user_management_service.exceptions.UserAlreadyExists.UserAlreadyExistsException;
import com.bit.user_management_service.exceptions.UserNotFound.UserNotFoundException;
import com.bit.user_management_service.service.EmailService;
import com.bit.user_management_service.service.UserService;
import com.bit.user_management_service.utils.PasswordGenerator;
import com.bit.user_management_service.utils.UserCodeGenerator;
import com.bit.user_management_service.validators.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final PasswordGenerator passwordGenerator;
    private final UserCodeGenerator userCodeGenerator;
    private final EmailValidator emailValidator;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void addUser(AddUserReq addUserReq) {
        if (!isExistingUser(addUserReq)){
            return;
        }

        validateEmail(addUserReq.getEmail());

        Set<Role> roles = mapRolesForAddUser(addUserReq);

        handleInitialAdmin(roles);

        String userCode = userCodeGenerator.createUserCode(addUserReq.getRoles(), findMaxId() + 1);
        String password = generatePassword(addUserReq.getEmail(), findMaxId() + 1);
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(password);

        User newUser = buildUser(addUserReq, userCode, encodedPassword, roles);

        userRepository.save(newUser);
        emailService.sendEmail(addUserReq.getEmail(), "Welcome!", "userCredentials-mail-template",
                newUser.getUserCode(), password, newUser.getFirstName(), newUser.getLastName());

        logger.info("User added successfully: {}", newUser.getUserCode());
        logger.info("User credentials sent to the user's email");
    }

    @Override
    public void updateUser(Long userId, UpdateUserReq updateUserReq){
        User existingUser = getUserById(userId);

        if(existingUser.isDeleted()){
            throw new UserNotFoundException("User is deleted: " + existingUser.getEmail());
        }

        if (!updateUserReq.getEmail().isEmpty()){
            validateEmail(updateUserReq.getEmail());
        }

        Set<Role> roles = mapRolesForUpdateUser(updateUserReq);

        updateExistingUser(existingUser, updateUserReq, roles);

        logger.info("User with ID {} updated successfully", userId);
    }

    @Override
    public void deleteUser(Long userId){
        User existingUser = getUserById(userId);

        existingUser.setDeleted(true);

        userRepository.save(existingUser);

        emailService.sendEmail(existingUser.getEmail(), "Thanks for your efforts",
                "terminationOfRelationship-mail-template", existingUser.getFirstName(), existingUser.getLastName());

        logger.info("User with ID {} deleted successfully", userId);
        logger.info("The user was informed via e-mail about the termination of his/her relationship with the company.");
    }

    private User buildUser(AddUserReq addUserReq, String userCode, String password, Set<Role> roles){
        return User.builder()
                .firstName(addUserReq.getFirstName())
                .lastName(addUserReq.getLastName())
                .email(addUserReq.getEmail())
                .userCode(userCode)
                .password(password)
                .roles(roles)
                .build();
    }

    private boolean isExistingUser(AddUserReq addUserReq){
        Optional<User> existingUser = userRepository.findByEmail(addUserReq.getEmail());

        if (existingUser.isPresent() && !existingUser.get().isDeleted()){
            throw new UserAlreadyExistsException("User already exists: " + existingUser.get().getEmail());
        } else if (existingUser.isPresent()){
            reactivateExistingUser(addUserReq, existingUser.get());
            return false;
        }
        return true;
    }

    private void reactivateExistingUser(AddUserReq addUserReq, User existingUser) {
        existingUser.setDeleted(false);

        String password = generatePassword(addUserReq.getEmail(), existingUser.getId());
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(password);

        existingUser.setRoles(mapRolesForAddUser(addUserReq));
        existingUser.setFirstName(addUserReq.getFirstName());
        existingUser.setLastName(addUserReq.getLastName());
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);
        emailService.sendEmail(existingUser.getEmail(), "Welcome Back!", "reHired-mail-template",
                existingUser.getUserCode(), password, existingUser.getFirstName(), existingUser.getLastName());

        logger.info("An existing user re-added to the system: {}", addUserReq.getEmail());
        logger.info("A welcome back mail sent to the user");
    }

    private void validateEmail(String email) {
        if (!emailValidator.isValidEmail(email)){
            throw new InvalidEmailException("Invalid email: " + email);
        }
    }

    private Set<Role> mapRolesForAddUser(AddUserReq addUserReq){
        return addUserReq.getRoles().stream()
                .map(this::findRoleByNameOrThrow)
                .collect(Collectors.toSet());
    }

    private Set<Role> mapRolesForUpdateUser(UpdateUserReq updateUserReq){
        return updateUserReq.getRoles().stream()
                .map(this::findRoleByNameOrThrow)
                .collect(Collectors.toSet());
    }

    private Role findRoleByNameOrThrow(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
    }

    private String generatePassword(String email, Long id) {
        return passwordGenerator.createPassword(email, id);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Long findMaxId(){
        Long maxId = userRepository.findMaxId();
        if (maxId == null) {
            logger.warn("User ID is null. Defaulting to ID 1.");
            maxId = 0L;

            return maxId;
        }
        return maxId;
    }

    private void updateExistingUser(User existingUser, UpdateUserReq updateUserReq, Set<Role> roles) {
        if (!updateUserReq.getFirstName().isEmpty()){
            existingUser.setFirstName(updateUserReq.getFirstName());
        }

        if (!updateUserReq.getLastName().isEmpty()){
            existingUser.setLastName(updateUserReq.getLastName());
        }

        if (!updateUserReq.getEmail().isEmpty()){
            existingUser.setEmail(updateUserReq.getEmail());
        }

        if (!updateUserReq.getRoles().isEmpty()){
            updateUserCodeIfRolesAreChanged(existingUser, updateUserReq);

            existingUser.setRoles(roles);
        }

        userRepository.save(existingUser);
    }

    private void updateUserCodeIfRolesAreChanged(User existingUser, UpdateUserReq updateUserReq){
        Set<String> existingRoleNames = existingUser.getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        if (!existingRoleNames.equals(updateUserReq.getRoles())){
            String updatedUserCode = userCodeGenerator.createUserCode(updateUserReq.getRoles(), existingUser.getId());
            existingUser.setUserCode(updatedUserCode);

            emailService.sendEmail(updateUserReq.getEmail(), "User Code Updated",
                    "updatedUserCode-mail-template", updatedUserCode, existingUser.getFirstName(),
                    existingUser.getLastName());

            logger.info("New user code created and sent to user's email");
        }
    }

    private void handleInitialAdmin(Set<Role> roles){
        Optional<User> initialAdmin = userRepository.findByEmail("admin@gmail.com");
        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");

        if (adminRole.isPresent() && roles.contains(adminRole.get()) && initialAdmin.isPresent()){
            userRepository.delete(initialAdmin.get());
            logger.info("Initial admin is deleted");
        }
    }
}
