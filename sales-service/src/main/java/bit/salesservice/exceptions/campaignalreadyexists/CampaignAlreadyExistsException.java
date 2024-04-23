package bit.salesservice.exceptions.campaignalreadyexists;

/**
 * This exception is thrown when a campaign with the same name already exists.
 */
public class CampaignAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a CampaignAlreadyExistsException with the specified error message.
     *
     * @param message The error message to be displayed when the exception is thrown.
     */
    public CampaignAlreadyExistsException(String message) {
        super(message);
    }
}
