package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveOrReturnProductFromBagReq {
    private Long checkoutId; // ID of the checkout associated with the shopping bag
    private String code; // Code or identifier of the product to be removed or returned
    private Integer quantity; // Quantity of the product to be removed or returned
}
