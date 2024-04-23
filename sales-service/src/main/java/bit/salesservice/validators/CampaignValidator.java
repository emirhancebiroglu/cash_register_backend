package bit.salesservice.validators;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.entity.Campaign;
import bit.salesservice.exceptions.invaliddiscountamount.InvalidDiscountAmountException;
import bit.salesservice.exceptions.invaliddiscounttype.InvalidDiscountTypeException;
import bit.salesservice.exceptions.invaliddurationdays.InvalidDurationDaysException;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.multiplecampaign.MultipleCampaignException;
import bit.salesservice.exceptions.nullcampaignname.NullCampaignNameException;
import bit.salesservice.repository.CampaignRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The CampaignValidator class provides methods to validate the parameters of a campaign.
 * It ensures that the discount amount, duration days, product codes, campaign name,
 * discount type, and needed quantity are valid.
 */
@Component
public class CampaignValidator {
    /**
     * Validates the parameters of a campaign DTO, including the discount amount, duration days,
     * product codes, campaign name, discount type, and needed quantity.
     *
     * @param addAndUpdateCampaignReq The request object containing campaign parameters.
     * @param campaignRepository      The repository for accessing campaign data.
     * @throws InvalidDurationDaysException    If the duration days are not within the valid range.
     * @throws MultipleCampaignException       If a product already has an associated campaign.
     * @throws InvalidDiscountAmountException If the discount amount is not positive.
     * @throws InvalidDiscountTypeException   If the discount type is not provided.
     * @throws NullCampaignNameException      If the campaign name is not provided.
     * @throws InvalidQuantityException       If the needed quantity is not positive.
     */
    public void validateCampaignDTO(AddAndUpdateCampaignReq addAndUpdateCampaignReq, CampaignRepository campaignRepository) {
        checkIfDiscountAmountNull(addAndUpdateCampaignReq.getDiscountAmount());
        checkIfDurationDaysNull(addAndUpdateCampaignReq.getDurationDays());
        validateProductCodes(addAndUpdateCampaignReq.getCodes(), campaignRepository);
        validateDurationDays(addAndUpdateCampaignReq);
        validateCampaignName(addAndUpdateCampaignReq.getName());
        validateDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());
        validateDiscountType(addAndUpdateCampaignReq.getDiscountType());
        validateNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());
    }

    /**
     * Validates the duration days parameter of the campaign.
     *
     * @param addAndUpdateCampaignReq The request object containing campaign parameters.
     * @throws InvalidDurationDaysException If the duration days are not within the valid range.
     */
    public void validateDurationDays(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        Integer durationDays = addAndUpdateCampaignReq.getDurationDays();

        if (durationDays <= 0) {
            throw new InvalidDurationDaysException("Duration of the campaign must be a positive integer");
        }

        if (durationDays > 360){
            throw new InvalidDurationDaysException("Duration of the campaign cannot be more than 1 year");
        }
    }

    /**
     * Validates the product codes parameter of the campaign.
     *
     * @param codes              The list of product codes associated with the campaign.
     * @param campaignRepository The repository for accessing campaign data.
     * @throws MultipleCampaignException If a product already has an associated campaign.
     */
    public void validateProductCodes(List<String> codes, CampaignRepository campaignRepository) {
        for (String code : codes) {
            List<Campaign> campaignList = campaignRepository.findByCodesContaining(code);
            if (!campaignList.isEmpty()) {
                throw new MultipleCampaignException("A product can only have one campaign: " + code);
            }
        }
    }

    /**
     * Validates the discount amount of a campaign.
     *
     * @param amount The discount amount to be validated.
     * @throws InvalidDiscountAmountException If the discount amount is not positive.
     */
    public void validateDiscountAmount(Double amount) {
        if (amount <= 0) {
            throw new InvalidDiscountAmountException("Discount amount cannot be 0 or negative");
        }
    }

    /**
     * Validates the discount type of a campaign.
     *
     * @param discountType The discount type to be validated.
     * @throws InvalidDiscountTypeException If the discount type is not provided.
     */
    public void validateDiscountType(String discountType) {
        if (discountType.isEmpty()) {
            throw new InvalidDiscountTypeException("No discount type provided");
        }
    }

    /**
     * Validates the needed quantity parameter of a campaign.
     *
     * @param quantity The needed quantity to be validated.
     * @throws InvalidQuantityException If the needed quantity is not positive.
     */
    public void validateNeededQuantity(Integer quantity) {
        if (quantity != null && (quantity <= 0)) {
                throw new InvalidQuantityException("Needed quantity cannot be 0 or negative");
        }
    }

    /**
     * Checks if the discount amount of a campaign is null.
     *
     * @param amount The discount amount to be checked.
     * @throws InvalidDiscountAmountException If the discount amount is not provided.
     */
    private void checkIfDiscountAmountNull(Double amount){
        if (amount == null) {
            throw new InvalidDiscountAmountException("No discount amount provided");
        }
    }

    /**
     * Validates the campaign name.
     *
     * @param name The campaign name to be validated.
     * @throws NullCampaignNameException If the campaign name is not provided.
     */
    private void validateCampaignName(String name) {
        if (name.isEmpty()) {
            throw new NullCampaignNameException("No campaign name provided");
        }
    }

    /**
     * Checks if the duration days of a campaign is null.
     *
     * @param durationDays The duration days to be checked.
     * @throws InvalidDurationDaysException If the duration days are not provided.
     */
    private static void checkIfDurationDaysNull(Integer durationDays) {
        if (durationDays == null) {
            throw new InvalidDurationDaysException("Duration of the campaign is not provided");
        }
    }
}