package bit.salesservice.dto;

import lombok.Data;

/**
 * DTO class representing a request to complete a checkout process.
 */
@Data
public class CompleteCheckoutReq {
    private String paymentMethod;
    private Double moneyTaken;
}
