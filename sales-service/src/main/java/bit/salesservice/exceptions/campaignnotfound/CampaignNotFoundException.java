package bit.salesservice.exceptions.campaignnotfound;

/**
 * A custom exception that is thrown when a campaign is not found.
 */
public class CampaignNotFoundException extends RuntimeException {

    /**
     * Constructs a CampaignNotFoundException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
