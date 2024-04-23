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
    private String code;
    private String name;
    private int quantity;
    private double price;
}
