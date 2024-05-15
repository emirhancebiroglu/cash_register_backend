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
    private Long id; // ID of the sale report
    private List<ProductDTO> products; // List of products sold in the sale
    private Double totalPrice; // Total price of the sale
    private PaymentMethod paymentMethod; // Payment method used for the sale
    private Double moneyTaken; // Amount of money received from the customer
    private Double change; // Change given to the customer
    private LocalDateTime completedDate; // Date and time when the sale was completed
    private LocalDateTime cancelledDate; // Date and time when the sale was cancelled (if applicable)
    private Double returnedMoney; // Amount of money returned for cancelled items
}
