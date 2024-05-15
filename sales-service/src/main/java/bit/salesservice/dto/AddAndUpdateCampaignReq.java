package bit.salesservice.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO class representing a request to add or update a campaign.
 */
@Data
public class AddAndUpdateCampaignReq {
    private String name; // Name of the campaign
    private Integer durationDays; // Duration of the campaign in days
    private List<String> codes; // List of codes associated with the campaign
    private String discountType; // Type of discount offered by the campaign
    private Double discountAmount; // Amount of discount offered by the campaign
    private Integer neededQuantity; // Needed quantity for the campaign
}
