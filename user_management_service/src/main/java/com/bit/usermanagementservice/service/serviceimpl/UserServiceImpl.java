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
import com.bit.usermanagementservice.exceptions.atleastoneroleneeded.AtLeastOneRoleNeededException;
import com.bit.usermanagementservice.exceptions.invalidemail.InvalidEmailException;
import com.bit.usermanagementservice.exceptions.invalidname.InvalidNameException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The UserServiceImpl class is an implementation of the UserService interface.
 * It provides methods to manage user entities including adding, updating, deleting, and reactivating users,
 * as well as fetching users and searching for users by name.
 * This class interacts with repositories, validators, and the email service to perform user-related operations.
 */
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
    private static final String DELETED = "deleted";
    private static final String FIRST_NAME = "firstName";


    /**
     * Adds a new user based on the provided user request.
     * This method performs the following steps:
     * 1. Checks if the user already exists.
     * 2. Validates the user data.
     * 3. Maps the user roles.
     * 4. Handles initial admin if required.
     * 5. Generates a unique user code.
     * 6. Generates a password for the user.
     * 7. Encodes the password.
     * 8. Builds the user entity.
     * 9. Saves the user entity to the repository.
     * 10. Sends user credentials to the authentication service.
     * 11. Sends user credentials to the user via email.
     *
     * @param addUserReq The request object containing user information to be added.
     * @throws UserAlreadyExistsException If the user already exists in the system.
     * @throws InvalidEmailException If the email provided is invalid.
     * @throws InvalidNameException If the first or last name provided is invalid.
     * @throws AtLeastOneRoleNeededException If no roles are specified for the user.
     * @throws RoleNotFoundException If any of the specified roles are not found in the system.
     */
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

    /**
     * Updates an existing user with the provided user ID and update request.
     * This method performs the following steps:
     * 1. Retrieves the existing user by ID.
     * 2. Checks if the user is already deleted. If deleted, throws a UserNotFoundException.
     * 3. Maps the updated user roles.
     * 4. Updates the existing user with the new information.
     * 5. Initializes the admin configuration.
     * 6. Handles initial admin if required.
     *
     * @param userId       The ID of the user to be updated.
     * @param updateUserReq The request object containing the updated user information.
     * @throws UserNotFoundException     If the user with the provided ID is not found or is already deleted.
     * @throws InvalidEmailException      If the email provided in the update request is invalid.
     * @throws InvalidNameException       If the first or last name provided in the update request is invalid.
     * @throws RoleNotFoundException      If any of the specified roles in the update request are not found in the system.
     */
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

    /**
     * Deletes a user with the provided user ID.
     * This method performs the following steps:
     * 1. Retrieves the existing user by ID.
     * 2. Checks if the user is already deleted. If not, sets the user's deleted status to true and saves the changes.
     * 3. Initializes the admin configuration.
     * 4. Sends information about the deleted user to the authentication service.
     * 5. Sends an email to inform the user about the termination of their relationship.
     *
     * @param userId The ID of the user to be deleted.
     * @throws UserNotFoundException         If the user with the provided ID is not found.
     * @throws UserAlreadyDeletedException   If the user is already deleted.
     */
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

    /**
     * Reactivates a user with the provided user ID.
     * This method performs the following steps:
     * 1. Retrieves the existing user by ID.
     * 2. Checks if the user is already deleted. If deleted, sets the deleted status to false.
     * 3. Generates a new password for the reactivated user.
     * 4. Saves the changes to the user entity.
     * 5. Sends information about the reactivated user to the authentication service.
     * 6. Initializes the admin configuration.
     * 7. Sends a welcome-back email to the reactivated user.
     *
     * @param userId The ID of the user to be reactivated.
     * @throws UserNotFoundException      If the user with the provided ID is not found.
     * @throws UserAlreadyActiveException If the user is already active.
     */
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

    /**
     * Retrieves a list of active or inactive users with pagination support.
     * This method fetches users from the database based on the provided page number and page size.
     *
     * @param pageNo        The page number of the results to retrieve.
     * @param pageSize      The number of users per page.
     * @param status        Lists the users by their status (deleted or not)
     * @param searchingTerm The search term to receive users by their names
     * @param sortBy        Declares how to sort users
     * @return A list of UserDTO objects representing the users on the specified page.
     */
    @Override
    public List<UserDTO> getUsers(int pageNo, int pageSize, String status, String searchingTerm, String sortBy, String sortOrder) {
        logger.info("Getting users...");

        Pageable pageable = applySort(pageNo, pageSize, sortBy, sortOrder);
        Page<User> userPage;

        validateStatus(status);

        if (status != null && searchingTerm != null){
            userPage = userRepository.findAllByisDeletedAndFirstNameContainingIgnoreCase(status.equalsIgnoreCase(DELETED), searchingTerm, pageable);
        }
        else if(status != null){
            userPage = userRepository.findAllByisDeleted(status.equalsIgnoreCase(DELETED), pageable);
        }
        else if(searchingTerm != null){
            userPage = userRepository.findAllByFirstNameContainingIgnoreCase(searchingTerm, pageable);
        }
        else{
            userPage = userRepository.findAll(pageable);
        }

        List<UserDTO> listUserDTOList = userPage.getContent().stream()
                .map(this::mapUserToDTO)
                .toList();

        logger.info("Users fetched successfully");

        return listUserDTOList;
    }

    private void validateStatus(String status) {
        if (status != null && (!status.equalsIgnoreCase(DELETED) && !status.equalsIgnoreCase("notDeleted"))) {
            throw new IllegalArgumentException("Status of user can be either deleted or notDeleted");
        }
    }

    private Pageable applySort(int pageNo, int pageSize, String sortBy, String sortOrder) {
        Pageable pageable;

        if (sortBy != null && sortBy.equals(FIRST_NAME)){
            pageable = PageRequest.of(pageNo, pageSize, sortOrder.equalsIgnoreCase("ASC") ? Sort.by(Sort.Direction.ASC, FIRST_NAME) : Sort.by(Sort.Direction.DESC, FIRST_NAME));
        }
        else{
            pageable = PageRequest.of(pageNo, pageSize);
        }

        return pageable;
    }

    /**
     * Builds a new User entity using the provided information.
     *
     * @param addUserReq The AddUserReq object containing the user's details.
     * @param userCode   The user code generated for the user.
     * @param password   The encrypted password for the user.
     * @param roles      The roles assigned to the user.
     * @return A new User entity with the provided details.
     */
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

    /**
     * Checks if a user already exists in the database based on the provided AddUserReq object.
     * If a user with the same email already exists, it throws a UserAlreadyExistsException.
     *
     * @param addUserReq The AddUserReq object containing the user's details.
     * @throws UserAlreadyExistsException if a user with the same email already exists.
     */
    public void checkIfUserExists(AddUserReq addUserReq){
        Optional<User> existingUser = userRepository.findByEmail(addUserReq.getEmail());

        if (existingUser.isPresent()){
            String userEmail = existingUser.get().getEmail();
            throw new UserAlreadyExistsException("User already exists: " + userEmail);
        }
    }

    /**
     * Maps the roles specified in the AddUserReq object to a set of Role entities.
     *
     * @param addUserReq The AddUserReq object containing the user's roles.
     * @return A set of Role entities corresponding to the roles specified in the AddUserReq object.
     */
    private Set<Role> mapRolesForAddUser(AddUserReq addUserReq){
        return addUserReq.getRoles().stream()
                .map(this::findRoleByNameOrThrow)
                .collect(Collectors.toSet());
    }

    /**
     * Maps the roles specified in the UpdateUserReq object to a set of Role entities.
     *
     * @param updateUserReq The UpdateUserReq object containing the updated user's roles.
     * @return A set of Role entities corresponding to the roles specified in the UpdateUserReq object.
     */
    private Set<Role> mapRolesForUpdateUser(UpdateUserReq updateUserReq){
        return updateUserReq.getRoles().stream()
                .map(this::findRoleByNameOrThrow)
                .collect(Collectors.toSet());
    }

    /**
     * Finds a role by its name in the RoleRepository. If the role is not found, it throws a RoleNotFoundException.
     *
     * @param roleName The name of the role to find.
     * @return The Role entity corresponding to the given role name.
     * @throws RoleNotFoundException if the role with the given name is not found.
     */
    private Role findRoleByNameOrThrow(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
    }


    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Generates a password for a user based on the provided email and ID.
     *
     * @param email The email of the user for whom the password is generated.
     * @param id    The ID of the user for whom the password is generated.
     * @return The generated password.
     */
    private String generatePassword(String email, Long id) {
        return passwordGenerator.createPassword(email, id);
    }

    /**
     * Resets the password for a user.
     *
     * @param user The user for whom the password is reset.
     * @return The new password after resetting.
     */
    private String resetUserPassword(User user) {
        logger.info("Resetting password...");
        String newPassword = generatePassword(user.getEmail(), user.getId());
        String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);
        logger.info("Password reset!");
        return newPassword;
    }

    /**
     * Finds the maximum user ID in the UserRepository.
     *
     * @return The maximum user ID found in the repository, or 1L if no user ID is found.
     */
    private Long findMaxId(){
        Long maxId = userRepository.findMaxId();
        if (maxId == null) {
            logger.warn("User ID is null. Defaulting to ID 1.");
            maxId = 1L;
        }
        return maxId;
    }

    /**
     * Updates the existing user with the information provided in the UpdateUserReq object and saves the changes to the repository.
     * If roles are changed, it updates the user code accordingly and sends the updated user information to the authentication service.
     *
     * @param existingUser  The existing user entity to be updated.
     * @param updateUserReq The UpdateUserReq object containing the updated user information.
     * @param roles         The set of roles assigned to the user.
     */
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

    /**
     * Updates the user code if the roles of the user are changed and sends the updated user code to the user's email.
     *
     * @param existingUser    The existing user entity.
     * @param updateUserReq   The UpdateUserReq object containing the updated user information.
     */
    private void updateUserCodeIfRolesAreChanged(User existingUser, UpdateUserReq updateUserReq){
        Set<String> existingRoleNames = existingUser.getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        if (!existingRoleNames.equals(updateUserReq.getRoles())){
            String updatedUserCode = userCodeGenerator.createUserCode(updateUserReq.getRoles(), existingUser.getId());
            existingUser.setUserCode(updatedUserCode);

            sendUpdatedUserCodeByEmail(existingUser, updatedUserCode);
        }
    }

    /**
     * Sends an email containing the updated user code to the user.
     *
     * @param user             The user entity.
     * @param updatedUserCode  The updated user code.
     */
    private void sendUpdatedUserCodeByEmail(User user, String updatedUserCode) {
        emailService.sendEmail(user.getEmail(), "User Code Updated",
                "updatedUserCode-mail-template", updatedUserCode, user.getFirstName(),
                user.getLastName());
        logger.info("A new user code has been created and sent to the user's email.");

    }

    /**
     * Sends the user credentials via email upon user registration.
     *
     * @param newUser    The newly registered user entity.
     * @param password   The password generated for the user.
     */
    private void sendUserCredentialsByEmail(User newUser, String password) {
        emailService.sendEmail(newUser.getEmail(), "Welcome!", "userCredentials-mail-template",
                newUser.getUserCode(), password, newUser.getFirstName(), newUser.getLastName());
        logger.info("The user credentials have been sent to the user via email.");
    }

    /**
     * Sends a termination message via email to the user.
     *
     * @param user  The user entity.
     */
    private void sendTerminationInfoByEmail(User user) {
        emailService.sendEmail(user.getEmail(), "Thanks for your efforts",
                "terminationOfRelationship-mail-template", user.getFirstName(), user.getLastName());
        logger.info("The user has been informed via email about the termination of their relationship.");
    }

    /**
     * Sends a welcome back message via email to the user with the new password upon reactivation.
     *
     * @param user         The user entity.
     * @param newPassword  The newly generated password for the user.
     */
    private void sendWelcomeBackMessageByEmail(User user, String newPassword) {
        emailService.sendEmail(user.getEmail(), "Welcome Back!", "reHired-mail-template",
                user.getUserCode(), newPassword, user.getFirstName(), user.getLastName());
        logger.info("A welcome-back email has been sent to the user: {}", user.getEmail());
    }

    /**
     * Checks if the provided set of roles contains the ROLE_ADMIN role. If it does, it sets the initial admin user as deleted,
     * saves the changes to the repository, and sends a deletion message to the authentication service.
     *
     * @param roles  The set of roles to be checked.
     */
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

    /**
     * Sends the user credentials to the authentication service.
     *
     * @param newUser  The newly registered user entity.
     */
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

    /**
     * Sends the updated user information to the authentication service.
     *
     * @param userId    The ID of the user whose information is updated.
     * @param email     The email of the user.
     * @param userCode  The user code of the user.
     * @param roles     The roles assigned to the user.
     */
    private void sendUpdatedUserInfoToAuthService(Long userId, String email,  String userCode, Set<Role> roles){
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO(
                userId,
                email,
                userCode,
                roles
        );

        credentialsProducer.sendMessage("user-update", userUpdateDTO);
    }

    /**
     * Sends the deleted user information to the authentication service.
     *
     * @param userId     The ID of the user who is deleted.
     * @param isDeleted  A boolean indicating if the user is deleted or not.
     */
    private void sendDeletedUserInfoToAuthService(Long userId, boolean isDeleted){
        UserSafeDeletionDTO userSafeDeletionDTO = new UserSafeDeletionDTO(
                userId,
                isDeleted
        );

        credentialsProducer.sendMessage("user-deletion", userSafeDeletionDTO);
    }

    /**
     * Sends the reactivated user information to the authentication service.
     *
     * @param userId     The ID of the user who is reactivated.
     * @param isDeleted  A boolean indicating if the user is deleted or not.
     * @param password   The password of the reactivated user.
     */
    private void sendReactivatedUserInfoToAuthService(Long userId, boolean isDeleted, String password){
        UserReactivateDTO userReactivateDTO = new UserReactivateDTO(
                userId,
                password,
                isDeleted
        );

        credentialsProducer.sendMessage("user-reactivate", userReactivateDTO);
    }

    /**
     * Maps the user entity to a UserDTO object.
     *
     * @param user  The user entity to be mapped.
     * @return      The UserDTO object mapped from the user entity.
     */
    private UserDTO mapUserToDTO(User user) {
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles()
        );
    }
}