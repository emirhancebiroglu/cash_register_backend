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
    private Long id;
    private Integer returnedQuantity;
    private Double returnedMoney;
    private Integer quantity;
    private Boolean returned;
}
