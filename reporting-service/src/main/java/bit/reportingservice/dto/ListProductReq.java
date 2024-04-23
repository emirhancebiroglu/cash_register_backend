package bit.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO representing a product in a report.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListProductReq {
    private String code;
    private String name;
    private String appliedCampaign;
    private Integer quantity;
    private Integer returnedQuantity;
    private boolean isReturned;
    private double price;
}
