package bit.salesservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a checkout entity in the sales system.
 */
@Entity
@Table(name = "checkout", schema = "sales")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "products")
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // Unique identifier of the checkout

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products; // List of products associated with the checkout

    @Column(name = "total_price")
    private Double totalPrice = 0D; // Total price of the checkout

    @Column(name = "change")
    private Double change = 0D; // Change given during the checkout process

    @Column(name = "money_taken")
    private Double moneyTaken = 0D; // Amount of money taken during the checkout process

    @Column(name = "returned_money")
    private Double returnedMoney = 0D; // Amount of money returned during the checkout process

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod; // Payment method used for the checkout

    @Column(name = "created_date")
    private LocalDateTime createdDate; // Date and time when the checkout was created
    @Column(name = "updated_date")
    private LocalDateTime updatedDate; // Date and time when the checkout was last updated

    @Column(name = "completed_date")
    private LocalDateTime completedDate; // Date and time when the checkout was completed

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate; // Date and time when the checkout was cancelled

    @Column(name = "is_cancelled", columnDefinition = "boolean default false")
    private boolean isCancelled; // Indicates whether the checkout is cancelled

    @Column(name = "is_completed", columnDefinition = "boolean default false")
    private boolean isCompleted; // Indicates whether the checkout is completed
}
