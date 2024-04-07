package bit.salesservice.dto.kafka;

import bit.salesservice.entity.PaymentMethod;
import bit.salesservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SaleReportDTO {
    private Long id;
    private List<Product> products;
    private Double totalPrice;
    private PaymentMethod paymentMethod;
    private Double moneyTaken;
    private Double change;
    private LocalDateTime completedDate;
    private LocalDateTime cancelledDate;
    private Double returnedMoney;

}
