package bit.salesservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "checkout", schema = "sales")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "change")
    private Double change;

    @Column(name = "money_taken")
    private Double moneyTaken;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Column(name = "is_cancelled", columnDefinition = "boolean default false")
    private boolean isCancelled;

    @Column(name = "is_completed", columnDefinition = "boolean default false")
    private boolean isCompleted;
}
