package bit.salesservice.exceptions.invaliddiscounttype;

/**
 * Exception thrown when an invalid discount type is encountered.
 */
public class InvalidDiscountTypeException extends RuntimeException {
    /**
     * Constructs a new InvalidDiscountTypeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidDiscountTypeException(String message) {
        super(message);
    }
}
