package bit.reportingservice.exceptions.invalidfilter;

/**
 * Custom exception to indicate that an invalid filter is provided.
 */
public class InvalidFilterException extends RuntimeException {
    /**
     * Constructs a new InvalidFilterException with the specified detail message.
     * @param message the detail message.
     */
    public InvalidFilterException(String message) {
        super(message);
    }
}
