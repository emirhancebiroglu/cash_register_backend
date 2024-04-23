package bit.salesservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a campaign entity in the sales system.
 */
@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "campaigns", schema = "sales")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_inactive", columnDefinition = "boolean default false")
    private boolean isInactive;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "duration_days")
    private int durationDays;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @ElementCollection
    @CollectionTable(name = "ProductCampaigns", joinColumns = @JoinColumn(name = "campaign_id"))
    @Column(name = "code")
    private List<String> codes;

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount;

    @Column(name = "needed_quantity", nullable = false)
    private Integer neededQuantity;
}
