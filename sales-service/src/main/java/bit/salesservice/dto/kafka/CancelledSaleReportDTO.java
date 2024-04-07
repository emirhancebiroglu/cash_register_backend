package bit.salesservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CancelledSaleReportDTO {
    private Long id;
    private boolean isCancelled;
    private LocalDateTime canceledDate;
    private Double returnedMoney;
}
