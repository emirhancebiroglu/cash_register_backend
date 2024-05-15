package bit.salesservice.utils;

import bit.salesservice.dto.kafka.CampaignDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for producing campaign-related information to Kafka topics.
 */
@Component
@AllArgsConstructor
public class CampaignProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(CampaignProducer.class);

    /**
     * Sends campaign data to the specified Kafka topic.
     *
     * @param topic       The Kafka topic to which the campaign data will be sent.
     * @param campaignDTO The campaign data to be sent.
     */
    public void sendCampaign(String topic, CampaignDTO campaignDTO){
        kafkaTemplate.send(topic, campaignDTO);
        logger.trace("Sale report sent to kafka topic: {}", topic);
    }
}
