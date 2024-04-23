package bit.salesservice.service;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;

import java.util.List;

/**
 * Service interface for managing campaign operations.
 */
public interface CampaignService {
    /**
     * Adds a new campaign.
     *
     * @param addAndUpdateCampaignReq the request containing the details of the campaign to add
     */
    void addCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq);

    /**
     * Updates an existing campaign.
     *
     * @param addAndUpdateCampaignReq the request containing the updated details of the campaign
     * @param campaignId              the ID of the campaign to update
     */
    void updateCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq, Long campaignId);

    /**
     * Inactivates a campaign.
     *
     * @param campaignId the ID of the campaign to inactivate
     */
    void inactivateCampaign(Long campaignId);

    /**
     * Reactivates a campaign.
     *
     * @param campaignId   the ID of the campaign to reactivate
     * @param durationDays the duration in days for the reactivated campaign
     */
    void reactivateCampaign(Long campaignId, Integer durationDays);

    /**
     * Retrieves a list of campaigns based on filtering criteria.
     *
     * @param pageNo        the page number
     * @param pageSize      the size of each page
     * @param discountType  the discount type to filter by
     * @param isInactive    the status of the campaigns (active or inactive)
     * @param searchingTerm the term to search for in campaign names
     * @param sortBy        the field to sort by
     * @param sortOrder     the order in which to sort (ascending or descending)
     * @return a list of campaigns based on the filtering criteria
     */
    List<ListCampaignsReq> getCampaigns(int pageNo, int pageSize, String discountType, String isInactive, String searchingTerm, String sortBy, String sortOrder);
}
