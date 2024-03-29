package com.bit.usermanagementservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "userCredentials:com.bit.usermanagementservice.dto.kafka.UserCredentialsDTO, " +
                "userSafeDeletion:com.bit.usermanagementservice.dto.kafka.UserSafeDeletionDTO, " +
                "userUpdate:com.bit.usermanagementservice.dto.kafka.UserUpdateDTO, " +
                "userReactivate:com.bit.usermanagementservice.dto.kafka.UserReactivateDTO");

        logProducerConfiguration(configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private void logProducerConfiguration(Map<String, Object> configProps) {
        configProps.forEach((key, value) -> logger.info("Kafka Producer Config - {}: {}", key, value));
    }
}
