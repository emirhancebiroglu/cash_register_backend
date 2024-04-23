package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing product data.
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