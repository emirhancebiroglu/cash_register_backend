package bit.reportingservice.utils;

import bit.reportingservice.dto.kafka.CampaignDTO;
import bit.reportingservice.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignConsumer {
    private final ReportingService reportingService;
    private static final Logger logger = LoggerFactory.getLogger(CampaignConsumer.class);

    @KafkaListener(topics = "campaign", groupId = "sales")
    public void receiveCampaign(CampaignDTO campaignDTO){
        logger.info("Received Sale Report: {}", campaignDTO);

        reportingService.saveCampaign(campaignDTO);
    }
}
