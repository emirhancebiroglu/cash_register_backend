package bit.salesservice.exceptions.nulldiscountamount;

public class NullDiscountAmountException extends RuntimeException {
    public NullDiscountAmountException(String message) {
        super(message);
    }
}
