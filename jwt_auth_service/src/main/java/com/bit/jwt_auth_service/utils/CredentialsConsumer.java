package com.bit.jwt_auth_service.utils;

import com.bit.jwt_auth_service.dto.kafka.UserCredentialsDTO;
import com.bit.jwt_auth_service.dto.kafka.UserReactivateDTO;
import com.bit.jwt_auth_service.dto.kafka.UserSafeDeletionDTO;
import com.bit.jwt_auth_service.dto.kafka.UserUpdateDTO;
import com.bit.jwt_auth_service.entity.User;
import com.bit.jwt_auth_service.exceptions.UserNotFound.UserNotFoundException;
import com.bit.jwt_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CredentialsConsumer {
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-credentials", groupId = "users")
    public void listen(UserCredentialsDTO userCredentials) {
        User user = User
                .builder()
                .id(userCredentials.getId())
                .userCode(userCredentials.getUserCode())
                .password(userCredentials.getPassword())
                .roles(userCredentials.getRoles())
                .isDeleted(userCredentials.isDeleted())
                .build();

        userRepository.save(user);
    }

    @KafkaListener(topics = "user-deletion", groupId = "users")
    public void listen(UserSafeDeletionDTO userSafeDeletionDTO) {
        User user = userRepository.findById(userSafeDeletionDTO.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setDeleted(userSafeDeletionDTO.isDeleted());

        userRepository.save(user);
    }

    @KafkaListener(topics = "user-update", groupId = "users")
    public void listen(UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userUpdateDTO.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUserCode(userUpdateDTO.getUserCode());
        user.setRoles(userUpdateDTO.getRoles());

        userRepository.save(user);
    }

    @KafkaListener(topics = "user-reactivate", groupId = "users")
    public void listen(UserReactivateDTO userReactivateDTO) {
        User user = userRepository.findById(userReactivateDTO.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(userReactivateDTO.getPassword());
        user.setDeleted(userReactivateDTO.isDeleted());

        userRepository.save(user);
    }
}