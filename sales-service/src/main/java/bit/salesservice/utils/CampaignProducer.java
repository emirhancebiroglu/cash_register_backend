package bit.salesservice.utils;

import bit.salesservice.dto.kafka.CampaignDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CampaignProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CampaignProducer.class);

    public void sendCampaign(String topic, CampaignDTO campaignDTO){
        kafkaTemplate.send(topic, campaignDTO);
        logger.info("Sale report sent to kafka topic: {}", topic);
    }
}