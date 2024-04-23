package bit.salesservice.exceptions.uncompletedcheckoutexception;

/**
 * Exception thrown when attempting to perform operations on an uncompleted checkout.
 */
public class UncompletedCheckoutException extends RuntimeException {
    /**
     * Constructs a new UncompletedCheckoutException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UncompletedCheckoutException(String message) {
        super(message);
    }
}
