package bit.salesservice.dto;

import lombok.Data;

@Data
public class CompleteCheckoutReq {
    private String paymentMethod;
    private Double moneyTaken;
    private Double change;
}
