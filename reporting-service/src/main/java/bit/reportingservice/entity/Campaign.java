package bit.reportingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Entity class representing a campaign.
 */
@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "campaigns", schema = "reports")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount;

    @Column(name = "needed_quantity", nullable = false)
    private Integer neededQuantity;
}