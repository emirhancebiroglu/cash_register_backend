package bit.salesservice.dto;

import lombok.Data;

/**
 * DTO class representing a request to complete a checkout process.
 */
@Data
public class CompleteCheckoutReq {
    private Double moneyTakenFromCash; // Amount of money taken from cash
    private Double moneyTakenFromCard; // Amount of money taken from card
}
