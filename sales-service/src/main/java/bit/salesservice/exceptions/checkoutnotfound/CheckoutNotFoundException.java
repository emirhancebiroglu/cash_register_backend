package bit.salesservice.exceptions.checkoutnotfound;

/**
 * Exception thrown when a checkout is not found.
 */
public class CheckoutNotFoundException extends RuntimeException {

    /**
     * Constructs a new instance of the CheckoutNotFoundException with the specified message.
     *
     * @param message the detail message. The content of this parameter is incorporated into the
     *                   completion of the exception's description
     */
    public CheckoutNotFoundException(String message) {
        super(message);
    }
}
