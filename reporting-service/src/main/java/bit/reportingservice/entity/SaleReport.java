package bit.reportingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity class representing a sale report.
 */
@Entity
@Table(name = "sale_report", schema = "reports")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "products")
public class SaleReport {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "saleReport", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products;

    @Column(name = "total_price")
    private Double totalPrice = 0D;

    @Column(name = "change")
    private Double change = 0D;

    @Column(name = "money_taken")
    private Double moneyTaken = 0D;

    @Column(name = "returned_money")
    private Double returnedMoney = 0D;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Column(name = "is_cancelled", columnDefinition = "boolean default false")
    private boolean cancelled;
}
