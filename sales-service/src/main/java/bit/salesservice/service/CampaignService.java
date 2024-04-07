package bit.salesservice.service;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;

import java.util.List;

public interface CampaignService {
    void addCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq);

    void updateCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq, Long campaignId);

    void inactivateCampaign(Long campaignId);

    void reactivateCampaign(Long campaignId, Integer durationDays);

    List<ListCampaignsReq> getAllCampaigns();
}
