package com.bit.jwtauthservice.utils;

import com.bit.jwtauthservice.dto.kafka.UserCredentialsDTO;
import com.bit.jwtauthservice.dto.kafka.UserReactivateDTO;
import com.bit.jwtauthservice.dto.kafka.UserSafeDeletionDTO;
import com.bit.jwtauthservice.dto.kafka.UserUpdateDTO;
import com.bit.jwtauthservice.entity.User;
import com.bit.jwtauthservice.exceptions.usernotfound.UserNotFoundException;
import com.bit.jwtauthservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CredentialsConsumer {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CredentialsConsumer.class);
    private static final String USER_NOT_FOUND_MESSAGE = "User not found!";

    @KafkaListener(topics = "user-credentials", groupId = "users")
    public void listen(UserCredentialsDTO userCredentials) {
        User user = new User(
                userCredentials.getId(),
                userCredentials.getEmail(),
                userCredentials.getUserCode(),
                userCredentials.getPassword(),
                userCredentials.getRoles(),
                userCredentials.isDeleted()
        );

        userRepository.save(user);
        logger.info("User credentials synchronized successfully: {}", user.getUserCode());
    }

    @KafkaListener(topics = "user-deletion", groupId = "users")
    public void listen(UserSafeDeletionDTO userSafeDeletionDTO) {
        User user = userRepository.findById(userSafeDeletionDTO.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setDeleted(userSafeDeletionDTO.isDeleted());

        userRepository.save(user);
        logger.info("User deletion status updated successfully: {}", user.getUserCode());
    }

    @KafkaListener(topics = "user-update", groupId = "users")
    public void listen(UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userUpdateDTO.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setEmail(userUpdateDTO.getEmail());
        user.setUserCode(userUpdateDTO.getUserCode());
        user.setRoles(userUpdateDTO.getRoles());

        userRepository.save(user);
        logger.info("User details updated successfully: {}", user.getUserCode());
    }

    @KafkaListener(topics = "user-reactivate", groupId = "users")
    public void listen(UserReactivateDTO userReactivateDTO) {
        User user = userRepository.findById(userReactivateDTO.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setPassword(userReactivateDTO.getPassword());
        user.setDeleted(userReactivateDTO.isDeleted());

        userRepository.save(user);
        logger.info("User reactivation status updated successfully: {}", user.getUserCode());
    }
}