package com.bit.usermanagementservice.service.serviceimpl;

import com.bit.usermanagementservice.config.AdminInitializationConfig;
import com.bit.usermanagementservice.config.PasswordEncoderConfig;
import com.bit.usermanagementservice.dto.adduser.AddUserReq;
import com.bit.usermanagementservice.dto.getuser.UserDTO;
import com.bit.usermanagementservice.dto.kafka.UserCredentialsDTO;
import com.bit.usermanagementservice.dto.kafka.UserReactivateDTO;
import com.bit.usermanagementservice.dto.kafka.UserSafeDeletionDTO;
import com.bit.usermanagementservice.dto.kafka.UserUpdateDTO;
import com.bit.usermanagementservice.dto.updateuser.UpdateUserReq;
import com.bit.usermanagementservice.entity.Role;
import com.bit.usermanagementservice.entity.User;
import com.bit.usermanagementservice.exceptions.rolenotfound.RoleNotFoundException;
import com.bit.usermanagementservice.exceptions.useralreadyactive.UserAlreadyActiveException;
import com.bit.usermanagementservice.exceptions.useralreadydeleted.UserAlreadyDeletedException;
import com.bit.usermanagementservice.exceptions.useralreadyexists.UserAlreadyExistsException;
import com.bit.usermanagementservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.usermanagementservice.repository.RoleRepository;
import com.bit.usermanagementservice.repository.UserRepository;
import com.bit.usermanagementservice.service.EmailService;
import com.bit.usermanagementservice.service.UserService;
import com.bit.usermanagementservice.utils.CredentialsProducer;
import com.bit.usermanagementservice.utils.PasswordGenerator;
import com.bit.usermanagementservice.utils.UserCodeGenerator;
import com.bit.usermanagementservice.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final EmailService emailService;
    private final AdminInitializationConfig adminInitializationConfig;
    private final CredentialsProducer credentialsProducer;
    private final UserValidator userValidator;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String FIRST_NAME = "firstName";

    @Override
    public void addUser(AddUserReq addUserReq) {
        logger.info("Adding user...");

        checkIfUserExists(addUserReq);
        userValidator.validateUserData(addUserReq);

        Set<Role> roles = mapRolesForAddUser(addUserReq);
        handleInitialAdmin(roles);

        String userCode = userCodeGenerator.createUserCode(addUserReq.getRoles(), findMaxId() + 1);

        String password = generatePassword(addUserReq.getEmail(), findMaxId() + 1);
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(password);

        User newUser = buildUser(addUserReq, userCode, encodedPassword, roles);

        userRepository.save(newUser);
        logger.info("User added successfully: {}", newUser.getUserCode());

        sendUserCredentialsToAuthService(newUser);

        sendUserCredentialsByEmail(newUser, password);
    }

    @Override
    public void updateUser(Long userId, UpdateUserReq updateUserReq){
        logger.info("Updating user...");

        User existingUser = findUserByIdOrThrow(userId);

        if(existingUser.isDeleted()){
            throw new UserNotFoundException("This user no longer exists: " + existingUser.getEmail());
        }

        Set<Role> roles = mapRolesForUpdateUser(updateUserReq);

        updateExistingUser(existingUser, updateUserReq, roles);
        adminInitializationConfig.initializeAdmin();
        handleInitialAdmin(roles);

        logger.info("User with ID {} updated successfully", userId);
    }

    @Override
    public void deleteUser(Long userId){
        logger.info("Deleting user...");

        User existingUser = findUserByIdOrThrow(userId);

        if (!existingUser.isDeleted()){
            existingUser.setDeleted(true);
            userRepository.save(existingUser);
            adminInitializationConfig.initializeAdmin();

            sendDeletedUserInfoToAuthService(existingUser.getId(), existingUser.isDeleted());

            logger.info("User with ID {} deleted successfully", userId);

            sendTerminationInfoByEmail(existingUser);
        }
        else{
            logger.error("User is already deleted");
            throw new UserAlreadyDeletedException("This user is already deleted");
        }
    }

    @Override
    public void reactivateUser(Long userId) {
        logger.info("Reactivating user...");

        User existingUser = findUserByIdOrThrow(userId);

        if (existingUser.isDeleted()){
            existingUser.setDeleted(false);

            String newPassword = resetUserPassword(existingUser);

            userRepository.save(existingUser);

            sendReactivatedUserInfoToAuthService(existingUser.getId(), existingUser.isDeleted(), existingUser.getPassword());

            handleInitialAdmin(existingUser.getRoles());
            logger.info("An existing user has been re-added to the system: {}", existingUser.getEmail());

            sendWelcomeBackMessageByEmail(existingUser, newPassword);
        }
        else{
            logger.error("This user is already active");
            throw new UserAlreadyActiveException("This user is already active");
        }
    }

    @Override
    public List<UserDTO> getUsers(int pageNo, int pageSize) {
        logger.info("Getting users...");

        Page<User> userPage = userRepository.findByisDeletedFalse(PageRequest.of(pageNo, pageSize, Sort.by(FIRST_NAME).ascending()));
        List<User> users = userPage.getContent();

        logger.info("Users fetched successfully");

        return users.stream()
                .map(this::mapUserToDTO)
                .toList();
    }

    @Override
    public List<UserDTO> getDeletedUsers(int pageNo, int pageSize) {
        logger.info("Getting users...");

        Page<User> userPage = userRepository.findByisDeletedTrue(PageRequest.of(pageNo, pageSize, Sort.by(FIRST_NAME).ascending()));
        List<User> users = userPage.getContent();

        logger.info("Users fetched successfully");

        return users.stream()
                .map(this::mapUserToDTO)
                .toList();
    }

    @Override
    public List<UserDTO> searchUserByName(String name, int pageNo, int pageSize) {
        logger.info("Searching for users ...");

        var idx = name.indexOf(' ');

        if (idx > -1){
            String firstNamePrefix = name.substring(0, idx);
            String lastNamePrefix = name.substring(idx + 1);

            Page<User> userPage = userRepository.findByFirstNameStartingWithIgnoreCaseAndLastNameStartingWithIgnoreCase(firstNamePrefix, lastNamePrefix, PageRequest.of(pageNo, pageSize, Sort.by(FIRST_NAME).ascending()));
            List<User> users = userPage.getContent();

            logger.info("Users found successfully");

            return users.stream()
                    .map(this::mapUserToDTO)
                    .toList();
        }

        Page<User> userPage = userRepository.findByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCase(name, name, PageRequest.of(pageNo, pageSize, Sort.by(FIRST_NAME).ascending()));
        List<User> users = userPage.getContent();

        logger.info("Users found successfully");

        return users.stream()
                .map(this::mapUserToDTO)
                .toList();
    }

    private User buildUser(AddUserReq addUserReq, String userCode, String password, Set<Role> roles){
        return new User(
                addUserReq.getFirstName(),
                addUserReq.getLastName(),
                addUserReq.getEmail(),
                userCode,
                password,
                roles
        );
    }

    public void checkIfUserExists(AddUserReq addUserReq){
        Optional<User> existingUser = userRepository.findByEmail(addUserReq.getEmail());

        if (existingUser.isPresent()){
            String userEmail = existingUser.get().getEmail();
            throw new UserAlreadyExistsException("User already exists: " + userEmail);
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

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private String generatePassword(String email, Long id) {
        return passwordGenerator.createPassword(email, id);
    }

    private String resetUserPassword(User user) {
        logger.info("Resetting password...");
        String newPassword = generatePassword(user.getEmail(), user.getId());
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);
        logger.info("Password reset!");
        return newPassword;
    }

    private Long findMaxId(){
        Long maxId = userRepository.findMaxId();
        if (maxId == null) {
            logger.warn("User ID is null. Defaulting to ID 1.");
            maxId = 1L;
        }
        return maxId;
    }

    private void updateExistingUser(User existingUser, UpdateUserReq updateUserReq, Set<Role> roles) {
        if (!updateUserReq.getFirstName().isEmpty()){
            userValidator.validateFirstName(updateUserReq.getFirstName());
            existingUser.setFirstName(updateUserReq.getFirstName());
        }

        if (!updateUserReq.getLastName().isEmpty()){
            userValidator.validateLastName(updateUserReq.getLastName());
            existingUser.setLastName(updateUserReq.getLastName());
        }

        if (!updateUserReq.getEmail().isEmpty()){
            userValidator.validateEmail(updateUserReq.getEmail());
            existingUser.setEmail(updateUserReq.getEmail());
        }

        if (!updateUserReq.getRoles().isEmpty()){
            updateUserCodeIfRolesAreChanged(existingUser, updateUserReq);
            existingUser.setRoles(roles);

            sendUpdatedUserInfoToAuthService(existingUser.getId(), existingUser.getEmail(), existingUser.getUserCode(), existingUser.getRoles());
        }

        userRepository.save(existingUser);
    }

    private void updateUserCodeIfRolesAreChanged(User existingUser, UpdateUserReq updateUserReq){
        Set<String> existingRoleNames = existingUser.getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        if (!existingRoleNames.equals(updateUserReq.getRoles())){
            String updatedUserCode = userCodeGenerator.createUserCode(updateUserReq.getRoles(), existingUser.getId());
            existingUser.setUserCode(updatedUserCode);

            sendUpdatedUserCodeByEmail(existingUser, updatedUserCode);
        }
    }

    private void sendUpdatedUserCodeByEmail(User user, String updatedUserCode) {
        emailService.sendEmail(user.getEmail(), "User Code Updated",
                "updatedUserCode-mail-template", updatedUserCode, user.getFirstName(),
                user.getLastName());
        logger.info("A new user code has been created and sent to the user's email.");

    }

    private void sendUserCredentialsByEmail(User newUser, String password) {
        emailService.sendEmail(newUser.getEmail(), "Welcome!", "userCredentials-mail-template",
                newUser.getUserCode(), password, newUser.getFirstName(), newUser.getLastName());
        logger.info("The user credentials have been sent to the user via email.");
    }

    private void sendTerminationInfoByEmail(User user) {
        emailService.sendEmail(user.getEmail(), "Thanks for your efforts",
                "terminationOfRelationship-mail-template", user.getFirstName(), user.getLastName());
        logger.info("The user has been informed via email about the termination of their relationship.");
    }

    private void sendWelcomeBackMessageByEmail(User user, String newPassword) {
        emailService.sendEmail(user.getEmail(), "Welcome Back!", "reHired-mail-template",
                user.getUserCode(), newPassword, user.getFirstName(), user.getLastName());
        logger.info("A welcome-back email has been sent to the user: {}", user.getEmail());
    }

    private void handleInitialAdmin(Set<Role> roles){
        if (roles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            userRepository.findByUserCode("admin")
                    .ifPresent(admin -> {
                        admin.setDeleted(true);

                        userRepository.save(admin);

                        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO(
                                admin.getId(),
                                admin.isDeleted()
                        );

                        credentialsProducer.sendMessage("user-deletion", userSafeDeletionDTO);
                        logger.info("The initial admin has been deleted.");
                    });
        }
    }

    private void sendUserCredentialsToAuthService(User newUser){
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getUserCode(),
                newUser.getPassword(),
                newUser.getRoles(),
                newUser.isDeleted()
        );

        credentialsProducer.sendMessage("user-credentials", userCredentialsDTO);
    }

    private void sendUpdatedUserInfoToAuthService(Long userId, String email,  String userCode, Set<Role> roles){
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(
                userId,
                email,
                userCode,
                roles
        );

        credentialsProducer.sendMessage("user-update", userUpdateDTO);
    }

    private void sendDeletedUserInfoToAuthService(Long userId, boolean isDeleted){
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO(
                userId,
                isDeleted
        );

        credentialsProducer.sendMessage("user-deletion", userSafeDeletionDTO);
    }

    private void sendReactivatedUserInfoToAuthService(Long userId, boolean isDeleted, String password){
        UserReactivateDTO userReactivateDTO = new UserReactivateDTO(
                userId,
                password,
                isDeleted
        );

        credentialsProducer.sendMessage("user-reactivate", userReactivateDTO);
    }

    private UserDTO mapUserToDTO(User user) {
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles()
        );
    }
}