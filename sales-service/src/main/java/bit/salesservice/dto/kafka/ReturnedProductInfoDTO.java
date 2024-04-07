package bit.salesservice.dto.kafka;

import bit.salesservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnedProductInfoDTO {
    private Product product;
    private Double returnedMoney;
}
