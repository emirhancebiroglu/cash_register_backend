package bit.reportingservice.dto.kafka;

import bit.reportingservice.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing information about a campaign.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDTO {
    private String name;
    private DiscountType discountType;
    private Double discountAmount;
    private Integer neededQuantity;
}
