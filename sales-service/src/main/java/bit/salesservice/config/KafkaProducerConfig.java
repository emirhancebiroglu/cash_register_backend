package bit.salesservice.config;

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
 * Configuration class for Kafka producer setup.
 */
@Configuration
public class KafkaProducerConfig {
    // Logger for logging Kafka producer configuration
    private static final Logger logger = LogManager.getLogger(KafkaProducerConfig.class);

    // Value fetched from application.properties for Kafka bootstrap servers
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a Kafka producer factory bean.
     *
     * @return the Kafka producer factory
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        // Configuration properties for Kafka producer
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Custom type mappings for JSON serialization
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "saleReport:bit.salesservice.dto.kafka.SaleReportDTO, " +
                "returnedProductInfo:bit.salesservice.dto.kafka.ReturnedProductInfoDTO, " +
                "cancelledSaleReport:bit.salesservice.dto.kafka.CancelledSaleReportDTO, " +
                "campaign:bit.salesservice.dto.kafka.CampaignDTO");

        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 20);

        // Log Kafka producer configuration
        logProducerConfiguration(configProps);

        // Create and return Kafka producer factory
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a Kafka template bean.
     *
     * @return the Kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        // Create and return Kafka template using the configured producer factory
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Logs the Kafka producer configuration.
     *
     * @param configProps the Kafka producer configuration properties
     */
    private void logProducerConfiguration(Map<String, Object> configProps) {
        // Log each configuration property and its value
        configProps.forEach((key, value) -> logger.trace("Kafka Producer Config - {}: {}", key, value));
    }
}
