package bit.salesservice.utils;

import bit.salesservice.entity.Campaign;
import bit.salesservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CampaignExpirationChecker {
    private final CampaignRepository campaignRepository;

    @Scheduled(fixedRate = 6000)
    public void checkAndUpdateCampaignStatus(){
        List<Campaign> campaigns = campaignRepository.findAllByisInactiveIsFalse();

        for (Campaign campaign : campaigns) {
            if (campaign.getEndDate().isBefore(LocalDateTime.now())){
                campaign.setInactive(true);
                campaignRepository.save(campaign);
            }
        }
    }
}
