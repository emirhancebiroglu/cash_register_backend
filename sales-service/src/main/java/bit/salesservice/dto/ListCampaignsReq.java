package bit.salesservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO class representing a request to list campaigns.
 */
@Data
public class ListCampaignsReq {
    private String name;
    private boolean isInactive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationDays;
    private List<String> codes;
    private String discountType;
    private Double discountAmount;
}
