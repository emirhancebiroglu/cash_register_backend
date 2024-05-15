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
    private Long id; // ID of the product
    private String code; // Code or identifier of the product
    private String name; // Name of the product
    private String appliedCampaign; // Name of the campaign applied to the product (if any)
    private Integer quantity; // Quantity of the product
    private Integer returnedQuantity; // Quantity of the product returned
    private boolean isReturned; // Indicates whether the product is returned
    private double price; // Price of the product
}