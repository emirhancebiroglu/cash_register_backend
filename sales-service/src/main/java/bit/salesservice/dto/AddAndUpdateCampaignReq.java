package bit.salesservice.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO class representing a request to add or update a campaign.
 */
@Data
public class AddAndUpdateCampaignReq {
    private String name;
    private Integer durationDays;
    private List<String> codes;
    private String discountType;
    private Double discountAmount;
    private Integer neededQuantity;
}
