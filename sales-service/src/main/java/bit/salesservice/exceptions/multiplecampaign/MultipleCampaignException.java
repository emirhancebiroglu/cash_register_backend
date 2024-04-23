package bit.salesservice.exceptions.multiplecampaign;

/**
 * Exception thrown when multiple campaigns are found.
 */
public class MultipleCampaignException extends RuntimeException {
    /**
     * Constructs a new MultipleCampaignException with the specified detail message.
     *
     * @param message the detail message.
     */
    public MultipleCampaignException(String message) {
        super(message);
    }
}
