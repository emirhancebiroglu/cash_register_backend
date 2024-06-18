package bit.salesservice.exceptions.unavailableservice;

/**
 * Exception thrown when product service is unavailable.
 */
public class UnavailableServiceException extends RuntimeException {
    /**
     * Constructs a new UnavailableServiceException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UnavailableServiceException(String message) {
        super(message);
    }
}