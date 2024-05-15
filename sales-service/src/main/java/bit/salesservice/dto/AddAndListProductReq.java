package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing a request to add a product to a shopping bag and list product details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAndListProductReq {
    private String code; // Code or identifier of the product
    private String name; // Name of the product
    private int quantity; // Quantity of the product
    private double price; // Price of the product
}
