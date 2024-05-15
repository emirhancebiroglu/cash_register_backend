package bit.salesservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing information about a returned product for Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnedProductInfoDTO {
    private Long id; // ID of the returned product
    private Integer returnedQuantity; // Quantity of the product returned
    private Double returnedMoney; // Amount of money refunded for the returned product
    private Integer quantity; // Total quantity of the product originally purchased
    private Boolean returned; // Indicates if the product was returned or not
}
