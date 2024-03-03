package com.bit.user_management_service.utils;

import com.bit.user_management_service.dto.UserCredentialsDTO;
import com.bit.user_management_service.dto.UserReactivateDTO;
import com.bit.user_management_service.dto.UserSafeDeletionDTO;
import com.bit.user_management_service.dto.UserUpdateDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CredentialsProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, UserCredentialsDTO userCredentials) {
        kafkaTemplate.send(topic, userCredentials);
    }

    public void sendMessage(String topic, UserSafeDeletionDTO userSafeDeletionDTO) {
        kafkaTemplate.send(topic,userSafeDeletionDTO);
    }

    public void sendMessage(String topic, UserUpdateDTO userUpdateDTO) {
        kafkaTemplate.send(topic, userUpdateDTO);
    }

    public void sendMessage(String topic, UserReactivateDTO userReactivateDTO) {
        kafkaTemplate.send(topic, userReactivateDTO);
    }
}