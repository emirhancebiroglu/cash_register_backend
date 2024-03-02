package com.bit.jwt_auth_service.utils;

import com.bit.jwt_auth_service.dto.UserCredentialsDTO;
import com.bit.jwt_auth_service.entity.User;
import com.bit.jwt_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CredentialsConsumer {
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-credentials", groupId = "user-credentials")
    public void listen(UserCredentialsDTO userCredentials) {
        System.out.println("Received message: " + userCredentials);

        User user = User
                .builder()
                .userCode(userCredentials.getUserCode())
                .password(userCredentials.getPassword())
                .roles(userCredentials.getRoles())
                .isDeleted(userCredentials.isDeleted())
                .build();

        userRepository.save(user);
    }
}