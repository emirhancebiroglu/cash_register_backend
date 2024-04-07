package bit.salesservice.exceptions.campaignalreadyexists;

public class CampaignAlreadyExistsException extends RuntimeException {
    public CampaignAlreadyExistsException(String message) {
        super(message);
    }
}
