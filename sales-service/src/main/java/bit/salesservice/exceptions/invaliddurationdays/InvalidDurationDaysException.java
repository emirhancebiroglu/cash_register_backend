package bit.salesservice.exceptions.invaliddurationdays;

/**
 * Exception thrown when an invalid duration days value is encountered.
 */
public class InvalidDurationDaysException extends RuntimeException {
    /**
     * Constructs a new InvalidDurationDaysException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidDurationDaysException(String message) {
        super(message);
    }
}
