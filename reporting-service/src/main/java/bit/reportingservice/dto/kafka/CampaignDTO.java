package bit.reportingservice.dto.kafka;

import bit.reportingservice.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CampaignDTO {
    private String name;
    private DiscountType discountType;
    private Double discountAmount;
    private Integer neededQuantity;
}
