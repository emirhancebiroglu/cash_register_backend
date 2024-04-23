package bit.salesservice.exceptions.completedcheckout;

/**
 * Exception thrown when a checkout is already completed.
 */
public class CompletedCheckoutException extends RuntimeException {

    /**
     * Constructs a new instance of the CompletedCheckoutException with the specified error message.
     *
     * @param message The error message to be displayed.
     */
    public CompletedCheckoutException(String message) {
        super(message);
    }
}
