package bit.reportingservice.dto;

import bit.reportingservice.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListReportsReq {
    private List<ListProductReq> products;
    private Double totalPrice;
    private PaymentMethod paymentMethod;
    private Double moneyTaken;
    private Double change;
    private LocalDateTime completedDate;
    private LocalDateTime cancelledDate;
    private Double returnedMoney;
    private Boolean cancelled;
}
