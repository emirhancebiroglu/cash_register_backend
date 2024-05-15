package bit.salesservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO class representing a request to list campaigns.
 */
@Data
public class ListCampaignsReq {
    private String name; // Name of the campaign
    private boolean isInactive; // Indicates whether the campaign is inactive
    private LocalDateTime startDate; // Start date of the campaign
    private LocalDateTime endDate; // End date of the campaign
    private Integer durationDays; // Duration of the campaign in days
    private List<String> codes; // List of codes associated with the campaign
    private String discountType; // Type of discount offered by the campaign
    private Double discountAmount; // Amount of discount offered by the campaign
}
