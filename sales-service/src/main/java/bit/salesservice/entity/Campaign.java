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
    private Long id; // Unique identifier of the campaign

    @Column(name = "name", nullable = false)
    private String name; // Name of the campaign

    @Column(name = "is_inactive", columnDefinition = "boolean default false")
    private boolean isInactive; // Indicates whether the campaign is inactive

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // Start date of the campaign

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // End date of the campaign

    @Column(name = "duration_days")
    private int durationDays; // Duration of the campaign in days

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate; // Date and time when the campaign was last updated

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ProductCampaigns", joinColumns = @JoinColumn(name = "campaign_id"))
    @Column(name = "code")
    private List<String> codes; // List of product codes associated with the campaign

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // Type of discount offered by the campaign

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount; // Amount of discount offered by the campaign

    @Column(name = "needed_quantity", nullable = false)
    private Integer neededQuantity; // Needed quantity for the campaign to be applicable
}