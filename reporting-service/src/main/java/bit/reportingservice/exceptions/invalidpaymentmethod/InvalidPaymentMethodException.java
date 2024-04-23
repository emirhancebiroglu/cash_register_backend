package bit.reportingservice.exceptions.invalidpaymentmethod;

/**
 * Custom exception to indicate that an invalid payment method is provided.
 */
public class InvalidPaymentMethodException extends RuntimeException {
    /**
     * Constructs a new InvalidPaymentMethodException with the specified detail message.
     * @param message the detail message.
     */
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}
