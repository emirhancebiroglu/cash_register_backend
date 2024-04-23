package bit.reportingservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing information about a product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String code;
    private String name;
    private String appliedCampaign;
    private Integer quantity;
    private Integer returnedQuantity;
    private boolean isReturned;
    private double price;
}
