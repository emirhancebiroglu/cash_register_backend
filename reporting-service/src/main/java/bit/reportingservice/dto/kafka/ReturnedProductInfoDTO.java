package bit.reportingservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
