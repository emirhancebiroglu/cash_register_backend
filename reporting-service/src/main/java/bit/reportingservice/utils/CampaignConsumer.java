package bit.reportingservice.utils;

import bit.reportingservice.dto.kafka.CampaignDTO;
import bit.reportingservice.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * This class is a Kafka listener that consumes campaign data from the "campaign" topic.
 * It is part of the {@link bit.reportingservice.utils} package and is annotated with {@code @Component}
 * to be managed by Spring.
 */
@Component
@RequiredArgsConstructor
public class CampaignConsumer {
    private final ReportingService reportingService;
    private static final Logger logger = LoggerFactory.getLogger(CampaignConsumer.class);

    /**
     * This method is a Kafka listener that listens for messages from the "campaign" topic.
     * When a message is received, it logs the received campaign data and saves it using the {@link #reportingService}.
     *
     * @param campaignDTO The {@link bit.reportingservice.dto.kafka.CampaignDTO} containing the campaign data.
     */
    @KafkaListener(topics = "campaign", groupId = "sales")
    public void receiveCampaign(CampaignDTO campaignDTO) {
        logger.trace("Received Sale Report: {}", campaignDTO);

        reportingService.saveCampaign(campaignDTO);
    }
}
