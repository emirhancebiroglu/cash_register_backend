package com.bit.usermanagementservice.utils;

import com.bit.usermanagementservice.dto.kafka.UserCredentialsDTO;
import com.bit.usermanagementservice.dto.kafka.UserReactivateDTO;
import com.bit.usermanagementservice.dto.kafka.UserSafeDeletionDTO;
import com.bit.usermanagementservice.dto.kafka.UserUpdateDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * The CredentialsProducer class is responsible for producing messages to Kafka topics related to user credentials.
 * It provides methods to send messages for user credentials, safe deletion, updates, and reactivation.
 */
@Component
@AllArgsConstructor
public class CredentialsProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(CredentialsProducer.class);

    /**
     * Sends a message for user credentials to the specified topic.
     *
     * @param topic the Kafka topic to send the message to.
     * @param userCredentials the user credentials DTO to send.
     */
    public void sendMessage(String topic, UserCredentialsDTO userCredentials) {
        kafkaTemplate.send(topic, userCredentials);
        logger.info("Sent message for UserCredentialsDTO to topic: {}", topic);
    }

    /**
     * Sends a message for safe deletion of a user to the specified topic.
     *
     * @param topic the Kafka topic to send the message to.
     * @param userSafeDeletionDTO the user safe deletion DTO to send.
     */
    public void sendMessage(String topic, UserSafeDeletionDTO userSafeDeletionDTO) {
        kafkaTemplate.send(topic, userSafeDeletionDTO);
        logger.info("Sent message for UserSafeDeletionDTO to topic: {}", topic);
    }

    /**
     * Sends a message for updating user information to the specified topic.
     *
     * @param topic the Kafka topic to send the message to.
     * @param userUpdateDTO the user update DTO to send.
     */
    public void sendMessage(String topic, UserUpdateDTO userUpdateDTO) {
        kafkaTemplate.send(topic, userUpdateDTO);
        logger.info("Sent message for UserUpdateDTO to topic: {}", topic);
    }

    /**
     * Sends a message for reactivating a user to the specified topic.
     *
     * @param topic the Kafka topic to send the message to.
     * @param userReactivateDTO the user reactivation DTO to send.
     */
    public void sendMessage(String topic, UserReactivateDTO userReactivateDTO) {
        kafkaTemplate.send(topic, userReactivateDTO);
        logger.info("Sent message for UserReactivateDTO to topic: {}", topic);
    }
}