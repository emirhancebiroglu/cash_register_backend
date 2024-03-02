package com.bit.user_management_service.utils;

import com.bit.user_management_service.dto.UserCredentialsDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CredentialsProducer {
    private KafkaTemplate<String, UserCredentialsDTO> kafkaTemplate;

    public void sendMessage(String topic, UserCredentialsDTO userCredentials) {
        kafkaTemplate.send(topic, userCredentials);
    }
}