package bit.salesservice.dto.kafka;

import bit.salesservice.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDTO {
    private String name; // Name of the campaign
    private DiscountType discountType; // Type of discount (e.g., percentage, fixed amount)
    private Double discountAmount; // Amount of discount
    private Integer neededQuantity; // Quantity needed to trigger the discount
}
