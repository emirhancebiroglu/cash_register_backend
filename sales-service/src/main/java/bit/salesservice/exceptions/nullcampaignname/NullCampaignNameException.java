package bit.salesservice.exceptions.nullcampaignname;

/**
 * Exception thrown when a null campaign name is encountered.
 */
public class NullCampaignNameException extends RuntimeException {
    /**
     * Constructs a new NullCampaignNameException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NullCampaignNameException(String message) {
        super(message);
    }
}
