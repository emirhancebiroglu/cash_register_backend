package bit.salesservice.service;

import bit.salesservice.dto.CampaignDTO;

public interface CampaignService {
    void addCampaign(CampaignDTO campaignDTO);

    void updateCampaign(CampaignDTO campaignDTO, Long campaignId);

    void inactivateCampaign(Long campaignId);

    void reactivateCampaign(Long campaignId, Integer durationDays);
}
