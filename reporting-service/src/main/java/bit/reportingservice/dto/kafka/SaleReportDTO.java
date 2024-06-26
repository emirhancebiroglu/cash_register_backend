package bit.reportingservice.dto.kafka;

import bit.reportingservice.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a sale report.
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
    private boolean cancelled;
}
