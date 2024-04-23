package bit.salesservice.exceptions.activecampaign;

/**
 * Custom exception class for ActiveCampaign related exceptions.
 */
public class ActiveCampaignException extends RuntimeException {

    /**
     * Constructs an instance of {@code ActiveCampaignException} with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ActiveCampaignException(String message) {
        super(message);
    }
}
