package bit.reportingservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing information about a cancelled sale report.
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
