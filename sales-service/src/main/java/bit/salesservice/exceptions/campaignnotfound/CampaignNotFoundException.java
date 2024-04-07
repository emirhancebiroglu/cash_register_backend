package bit.salesservice.exceptions.campaignnotfound;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
