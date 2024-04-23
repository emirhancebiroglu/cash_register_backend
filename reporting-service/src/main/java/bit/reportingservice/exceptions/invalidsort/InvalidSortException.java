package bit.reportingservice.exceptions.invalidsort;

/**
 * Custom exception to indicate that an invalid sorting parameter is provided.
 */
public class InvalidSortException extends RuntimeException {
    /**
     * Constructs a new InvalidSortException with the specified detail message.
     * @param message the detail message.
     */
    public InvalidSortException(String message) {
        super(message);
    }
}
