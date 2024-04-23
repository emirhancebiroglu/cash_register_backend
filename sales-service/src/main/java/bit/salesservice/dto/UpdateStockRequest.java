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
    /**
     * A map containing product IDs as keys and corresponding quantities as values.
     */
    private Map<String, Integer> productsIdWithQuantity;

    /**
     * A boolean indicating whether the stock quantities should be decreased.
     */
    private boolean shouldDecrease;
}
