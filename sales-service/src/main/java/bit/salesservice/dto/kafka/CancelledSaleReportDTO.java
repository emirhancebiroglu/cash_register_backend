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
    private Long id; // ID of the cancelled sale report
    private boolean isCancelled; // Indicates if the sale was cancelled
    private LocalDateTime canceledDate; // Date and time when the sale was cancelled
    private Double returnedMoney; // Amount of money returned for the cancelled sale
}
