package bit.salesservice.dto.kafka;

import bit.salesservice.dto.ProductDTO;
import bit.salesservice.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO class representing a sale report for Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleReportDTO {
    private Long id;
    private List<ProductDTO> products;
    private Double totalPrice;
    private PaymentMethod paymentMethod;
    private Double moneyTaken;
    private Double change;
    private LocalDateTime completedDate;
    private LocalDateTime cancelledDate;
    private Double returnedMoney;
}
