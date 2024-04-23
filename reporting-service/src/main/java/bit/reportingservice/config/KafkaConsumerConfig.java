package bit.reportingservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Kafka consumer properties and listener container factory.
 */
@Configuration
public class KafkaConsumerConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a Kafka consumer factory.
     *
     * @return the Kafka ConsumerFactory configured with the necessary properties
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        logger.info("Creating Kafka consumer factory...");

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "sales");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.TYPE_MAPPINGS, "saleReport:bit.reportingservice.dto.kafka.SaleReportDTO, " +
                "returnedProductInfo:bit.reportingservice.dto.kafka.ReturnedProductInfoDTO, " +
                "cancelledSaleReport:bit.reportingservice.dto.kafka.CancelledSaleReportDTO, " +
                "campaign:bit.reportingservice.dto.kafka.CampaignDTO");

        logger.info("Kafka consumer factory created successfully.");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Creates a Kafka listener container factory.
     *
     * @return the ConcurrentKafkaListenerContainerFactory configured with the Kafka consumer factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        logger.info("Creating Kafka listener container factory...");
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        logger.info("Kafka listener container factory created successfully.");

        return factory;
    }
}