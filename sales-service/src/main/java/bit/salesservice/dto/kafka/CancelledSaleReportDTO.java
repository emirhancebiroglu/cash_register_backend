package bit.salesservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO class representing a cancelled sale report for Kafka messaging.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelledSaleReportDTO {
    private Long id;
    private boolean isCancelled;
    private LocalDateTime canceledDate;
    private Double returnedMoney;
}
