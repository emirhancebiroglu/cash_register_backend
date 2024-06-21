package bit.salesservice.service;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.exceptions.activecampaign.ActiveCampaignException;
import bit.salesservice.exceptions.campaignnotfound.CampaignNotFoundException;
import bit.salesservice.exceptions.inactivecampaign.InactiveCampaignException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;

import java.util.List;

/**
 * Service interface for managing campaign operations.
 */
public interface CampaignService {
    /**
     * Method to add a new campaign to the system.
     *
     * @param addAndUpdateCampaignReq The request object containing the details of the campaign to be added.
     * @throws ProductNotFoundException If any of the required products for the campaign are not found.
     */
    void addCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq);

    /**
     * Updates an existing campaign in the database.
     *
     * @param addAndUpdateCampaignReq The request object containing the updated campaign details.
     * @param campaignId The ID of the campaign to be updated.
     */
    void updateCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq, Long campaignId);

    /**
     * Method to inactivate a campaign.
     *
     * @param campaignId The unique identifier of the campaign to be inactivated.
     * @throws CampaignNotFoundException If the campaign with the given id is not found.
     * @throws InactiveCampaignException If the campaign is already inactive.
     */
    void inactivateCampaign(Long campaignId);

    /**
     * Reactivates a campaign by setting its status to active.
     *
     * @param campaignId The unique identifier of the campaign to be reactivated.
     * @param durationDays The duration of the campaign in days.
     *
     * @throws CampaignNotFoundException If the campaign with the given campaignId is not found.
     * @throws ActiveCampaignException If the campaign is already active.
     */
    void reactivateCampaign(Long campaignId, Integer durationDays);

    /**
     * Retrieves a list of campaigns based on the provided parameters.
     *
     * @param pageNo The page number for pagination.
     * @param pageSize The number of campaigns per page.
     * @param discountType The discount type to filter by.
     * @param isInactive The status of the campaigns to filter by.
     * @param searchingTerm The term to search for in the campaign names.
     * @param sortBy The field to sort the campaigns by.
     * @param sortOrder The order to sort the campaigns by.
     * @return A list of {@link ListCampaignsReq} representing the campaigns.
     */
    List<ListCampaignsReq> getCampaigns(int pageNo, int pageSize, String discountType, String isInactive, String searchingTerm, String sortBy, String sortOrder);
}