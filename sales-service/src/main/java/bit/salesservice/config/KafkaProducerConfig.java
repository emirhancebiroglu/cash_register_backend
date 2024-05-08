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
    private static final Logger logger = LogManager.getLogger(KafkaProducerConfig.class);
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Creates a Kafka producer factory bean.
     *
     * @return the Kafka producer factory
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "saleReport:bit.salesservice.dto.kafka.SaleReportDTO, " +
                "returnedProductInfo:bit.salesservice.dto.kafka.ReturnedProductInfoDTO, " +
                "cancelledSaleReport:bit.salesservice.dto.kafka.CancelledSaleReportDTO, " +
                "campaign:bit.salesservice.dto.kafka.CampaignDTO");

        logProducerConfiguration(configProps);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a Kafka template bean.
     *
     * @return the Kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Logs the Kafka producer configuration.
     *
     * @param configProps the Kafka producer configuration properties
     */
    private void logProducerConfiguration(Map<String, Object> configProps) {
        configProps.forEach((key, value) -> logger.info("Kafka Producer Config - {}: {}", key, value));
    }
}
