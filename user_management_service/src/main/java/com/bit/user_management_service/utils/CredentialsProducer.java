package com.bit.user_management_service.utils;

import com.bit.user_management_service.dto.kafka.UserCredentialsDTO;
import com.bit.user_management_service.dto.kafka.UserReactivateDTO;
import com.bit.user_management_service.dto.kafka.UserSafeDeletionDTO;
import com.bit.user_management_service.dto.kafka.UserUpdateDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CredentialsProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CredentialsProducer.class);


    public void sendMessage(String topic, UserCredentialsDTO userCredentials) {
        kafkaTemplate.send(topic, userCredentials);
        logger.info("Sent message for UserCredentialsDTO to topic: {}", topic);
    }

    public void sendMessage(String topic, UserSafeDeletionDTO userSafeDeletionDTO) {
        kafkaTemplate.send(topic,userSafeDeletionDTO);
        logger.info("Sent message for UserSafeDeletionDTO to topic: {}", topic);
    }

    public void sendMessage(String topic, UserUpdateDTO userUpdateDTO) {
        kafkaTemplate.send(topic, userUpdateDTO);
        logger.info("Sent message for UserUpdateDTO to topic: {}", topic);
    }

    public void sendMessage(String topic, UserReactivateDTO userReactivateDTO) {
        kafkaTemplate.send(topic, userReactivateDTO);
        logger.info("Sent message for UserReactivateDTO to topic: {}", topic);
    }
}