package com.bit.jwt_auth_service.utils;


import com.bit.jwt_auth_service.dto.kafka.UserDetailsDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserDetailsProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, UserDetailsDTO userDetailsDTO) {
        kafkaTemplate.send(topic, userDetailsDTO);
    }
}
