package com.bit.user_management_service.service.serviceImpl;

import com.bit.sharedClasses.config.PasswordEncoderConfig;
import com.bit.sharedClasses.entity.Role;
import com.bit.sharedClasses.entity.User;
import com.bit.sharedClasses.repository.RoleRepository;
import com.bit.sharedClasses.repository.UserRepository;
import com.bit.user_management_service.config.AdminInitializationConfig;
import com.bit.user_management_service.dto.AddUser.AddUserReq;
import com.bit.user_management_service.dto.UpdateUser.UpdateUserReq;
import com.bit.user_management_service.dto.UserCredentialsDTO;
import com.bit.user_management_service.exceptions.InvalidEmail.InvalidEmailException;
import com.bit.user_management_service.exceptions.InvalidName.InvalidNameException;
import com.bit.user_management_service.exceptions.RoleNotFound.RoleNotFoundException;
import com.bit.user_management_service.exceptions.UserAlreadyActive.UserAlreadyActiveException;
import com.bit.user_management_service.exceptions.UserAlreadyDeleted.UserAlreadyDeletedException;
import com.bit.user_management_service.exceptions.UserAlreadyExists.UserAlreadyExistsException;
import com.bit.user_management_service.exceptions.UserNotFound.UserNotFoundException;
import com.bit.user_management_service.service.EmailService;
import com.bit.user_management_service.service.UserService;
import com.bit.user_management_service.utils.CredentialsProducer;
import com.bit.user_management_service.utils.PasswordGenerator;
import com.bit.user_management_service.utils.UserCodeGenerator;
import com.bit.user_management_service.validators.EmailValidator;
import com.bit.user_management_service.validators.NameValidator;
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
    private final NameValidator nameValidator;
    private final EmailValidator emailValidator;
    private final EmailService emailService;
    private final AdminInitializationConfig adminInitializationConfig;
    private final CredentialsProducer credentialsProducer;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void addUser(AddUserReq addUserReq) {
        checkIfUserExists(addUserReq);
        validateUserData(addUserReq);

        Set<Role> roles = mapRolesForAddUser(addUserReq);
        handleInitialAdmin(roles);

        String userCode = userCodeGenerator.createUserCode(addUserReq.getRoles(), findMaxId() + 1);

        String password = generatePassword(addUserReq.getEmail(), findMaxId() + 1);
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(password);

        User newUser = buildUser(addUserReq, userCode, encodedPassword, roles);

        userRepository.save(newUser);
        logger.info("User added successfully: {}", newUser.getUserCode());

        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO();
        userCredentialsDTO.setUserCode(newUser.getUserCode());
        userCredentialsDTO.setPassword(newUser.getPassword());
        userCredentialsDTO.setRoles(newUser.getRoles());

        credentialsProducer.sendMessage("user-credentials", userCredentialsDTO);

        sendUserCredentialsByEmail(newUser, password);
    }

    @Override
    public void updateUser(Long userId, UpdateUserReq updateUserReq){
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
        User existingUser = findUserByIdOrThrow(userId);

        if (!existingUser.isDeleted()){
            existingUser.setDeleted(true);
            userRepository.save(existingUser);
            adminInitializationConfig.initializeAdmin();
            logger.info("User with ID {} deleted successfully", userId);

            sendTerminationInfoByEmail(existingUser);
        }
        else{
            logger.error("User is already deleted");
            throw new UserAlreadyDeletedException("This user is already deleted");
        }
    }

    @Override
    public void reactivateUser(Long user_id) {
        User existingUser = findUserByIdOrThrow(user_id);

        if (existingUser.isDeleted()){
            existingUser.setDeleted(false);

            String newPassword = resetUserPassword(existingUser);

            userRepository.save(existingUser);
            handleInitialAdmin(existingUser.getRoles());
            logger.info("An existing user has been re-added to the system: {}", existingUser.getEmail());

            sendWelcomeBackMessageByEmail(existingUser, newPassword);
        }
        else{
            logger.error("This user is already active");
            throw new UserAlreadyActiveException("This user is already active");
        }
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

    private void checkIfUserExists(AddUserReq addUserReq){
        Optional<User> existingUser = userRepository.findByEmail(addUserReq.getEmail());

        if (existingUser.isPresent()){
            String userEmail = existingUser.get().getEmail();
            throw new UserAlreadyExistsException("User already exists: " + userEmail);
        }
    }

    private void validateUserData(AddUserReq addUserReq){
        validateEmail(addUserReq.getEmail());
        validateFirstName(addUserReq.getFirstName());
        validateLastName(addUserReq.getLastName());
    }

    private void validateEmail(String email) {
        if (!emailValidator.isValidEmail(email)){
            throw new InvalidEmailException("Invalid email: " + email);
        }
    }

    private void validateFirstName(String firstName){
        if (!nameValidator.validateFirstName(firstName)){
            throw new InvalidNameException("Invalid first name: " + firstName);
        }
    }

    private void validateLastName(String lastName){
        if (!nameValidator.validateLastName(lastName)){
            throw new InvalidNameException("Invalid last name: " + lastName);
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
        String newPassword = generatePassword(user.getEmail(), user.getId());
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);
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
            validateFirstName(updateUserReq.getFirstName());
            existingUser.setFirstName(updateUserReq.getFirstName());
        }

        if (!updateUserReq.getLastName().isEmpty()){
            validateLastName(updateUserReq.getLastName());
            existingUser.setLastName(updateUserReq.getLastName());
        }

        if (!updateUserReq.getEmail().isEmpty()){
            validateEmail(updateUserReq.getEmail());
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
            userRepository.findByEmail("admin@gmail.com")
                    .ifPresent(admin -> {
                        userRepository.delete(admin);
                        logger.info("The initial admin has been deleted.");
                    });
        }
    }
}