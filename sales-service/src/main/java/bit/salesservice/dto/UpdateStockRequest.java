package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * DTO class representing a request to update stock quantities.
 */
@Data
@AllArgsConstructor
public class UpdateStockRequest {
    private Map<String, Integer> productsIdWithQuantity; // Map containing product IDs as keys and quantities as values
    private boolean shouldDecrease; // Indicates whether the stock quantities should be decreased
}
