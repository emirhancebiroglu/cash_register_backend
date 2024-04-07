package bit.salesservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CampaignReq {
    private String name;
    private boolean isInactive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationDays;
    private List<String> codes;
    private String discountType;
    private Double discountAmount;
}
