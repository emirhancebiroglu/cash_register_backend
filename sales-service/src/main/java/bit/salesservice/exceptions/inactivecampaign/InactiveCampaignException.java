package bit.salesservice.exceptions.inactivecampaign;

/**
 * Exception thrown when a campaign is inactive.
 */
public class InactiveCampaignException extends RuntimeException {

    /**
     * Constructs an instance of InactiveCampaignException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public InactiveCampaignException(String message) {
        super(message);
    }
}
