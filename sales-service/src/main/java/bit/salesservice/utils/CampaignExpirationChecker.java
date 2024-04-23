package bit.salesservice.utils;

import bit.salesservice.entity.Campaign;
import bit.salesservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CampaignExpirationChecker {
    private final CampaignRepository campaignRepository;
    private static final Logger logger = LoggerFactory.getLogger(CampaignExpirationChecker.class);

    @Scheduled(fixedRate = 3600000)
    public void checkAndUpdateCampaignStatus(){

        List<Campaign> campaigns = campaignRepository.findAllByisInactiveIsFalse();

        for (Campaign campaign : campaigns) {
            if (campaign.getEndDate().isBefore(LocalDateTime.now())){
                logger.info("Campaign {} is expired, inactivating...", campaign.getName());
                campaign.setInactive(true);
                campaignRepository.save(campaign);
                logger.info("Campaign {} inactivated successfully", campaign.getName());
            }
        }
    }
}
