package bit.salesservice.exceptions.invalidpaymentmethod;

public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}
