package bit.salesservice.exceptions.invalidquantity;

/**
 * Exception thrown when an invalid quantity is encountered.
 */
public class InvalidQuantityException extends RuntimeException {
    /**
     * Constructs a new InvalidQuantityException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidQuantityException(String message) {
        super(message);
    }
}
