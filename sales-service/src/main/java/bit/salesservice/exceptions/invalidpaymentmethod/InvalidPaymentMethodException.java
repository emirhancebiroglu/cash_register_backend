package bit.salesservice.exceptions.invalidpaymentmethod;

/**
 * Exception thrown when an invalid payment method is encountered.
 */
public class InvalidPaymentMethodException extends RuntimeException {
    /**
     * Constructs a new InvalidPaymentMethodException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}
