package com.bit.jwtauthservice.utils;

import com.bit.jwtauthservice.dto.kafka.UserCredentialsDTO;
import com.bit.jwtauthservice.dto.kafka.UserReactivateDTO;
import com.bit.jwtauthservice.dto.kafka.UserSafeDeletionDTO;
import com.bit.jwtauthservice.dto.kafka.UserUpdateDTO;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer responsible for handling user-related messages received from Kafka topics.
 */
@Component
@RequiredArgsConstructor
public class CredentialsConsumer {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(CredentialsConsumer.class);
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    /**
     * Listens for user credentials messages from the Kafka topic "user-credentials" and synchronizes the user credentials.
     *
     * @param userCredentials The user credentials DTO containing the user information.
     */
    @KafkaListener(topics = "user-credentials", groupId = "users")
    public void listen(UserCredentialsDTO userCredentials) {
        logger.trace("Synchronizing user credentials");

        User user = new User(
                userCredentials.getId(),
                userCredentials.getEmail(),
                userCredentials.getUserCode(),
                userCredentials.getPassword(),
                userCredentials.getRoles(),
                userCredentials.isDeleted()
        );

        userRepository.save(user);

        logger.trace("User credentials synchronized successfully: {}", user.getUserCode());
    }

    /**
     * Listens for user safe deletion messages from the Kafka topic "user-deletion" and updates the deletion status of the user.
     *
     * @param userSafeDeletionDTO The user safe deletion DTO containing the user ID and deletion status.
     */
    @KafkaListener(topics = "user-deletion", groupId = "users")
    public void listen(UserSafeDeletionDTO userSafeDeletionDTO) {
        logger.trace("Updating user deletion status");

        User user = userRepository.findById(userSafeDeletionDTO.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setDeleted(userSafeDeletionDTO.isDeleted());

        userRepository.save(user);

        logger.trace("User deletion status updated successfully: {}", user.getUserCode());
    }

    /**
     * Listens for user update messages from the Kafka topic "user-update" and updates the user details.
     *
     * @param userUpdateDTO The user update DTO containing the updated user information.
     */
    @KafkaListener(topics = "user-update", groupId = "users")
    public void listen(UserUpdateDTO userUpdateDTO) {
        logger.trace("Updating user details");

        User user = userRepository.findById(userUpdateDTO.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setEmail(userUpdateDTO.getEmail());
        user.setUserCode(userUpdateDTO.getUserCode());
        user.setRoles(userUpdateDTO.getRoles());

        userRepository.save(user);

        logger.trace("User details updated successfully: {}", user.getUserCode());
    }

    /**
     * Listens for user reactivate messages from the Kafka topic "user-reactivate" and updates the user reactivation status.
     *
     * @param userReactivateDTO The user reactivate DTO containing the user ID, new password, and reactivation status.
     */
    @KafkaListener(topics = "user-reactivate", groupId = "users")
    public void listen(UserReactivateDTO userReactivateDTO) {
        logger.trace("Updating user reactivation status");

        User user = userRepository.findById(userReactivateDTO.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setPassword(userReactivateDTO.getPassword());
        user.setDeleted(userReactivateDTO.isDeleted());

        userRepository.save(user);

        logger.trace("User reactivation status updated successfully: {}", user.getUserCode());
    }
}