package com.bit.usermanagementservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for setting up Kafka producer.
 */
@Configuration
public class KafkaProducerConfig {
    private static final Logger logger = LogManager.getLogger(KafkaProducerConfig.class);
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a Kafka Producer factory.
     *
     * @return ProducerFactory instance configured with the provided properties.
     */
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

        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 20);

        logProducerConfiguration(configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for sending messages to Kafka topics.
     *
     * @return KafkaTemplate instance configured with the ProducerFactory.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Logs the Kafka producer configuration.
     *
     * @param configProps The Kafka producer configuration properties.
     */
    private void logProducerConfiguration(Map<String, Object> configProps) {
        configProps.forEach((key, value) -> logger.info("Kafka Producer Config - {}: {}", key, value));
    }
}
