package bit.salesservice.exceptions.invalidstatustype;

/**
 * Exception thrown when an invalid status type is encountered.
 */
public class InvalidStatusTypeException extends RuntimeException {
    /**
     * Constructs a new InvalidStatusTypeException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidStatusTypeException(String message) {
        super(message);
    }
}
