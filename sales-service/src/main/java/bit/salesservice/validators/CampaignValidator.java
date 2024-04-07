package bit.salesservice.validators;

import bit.salesservice.dto.CampaignDTO;
import bit.salesservice.entity.Campaign;
import bit.salesservice.exceptions.invaliddiscountamount.InvalidDiscountAmountException;
import bit.salesservice.exceptions.invaliddiscounttype.InvalidDiscountTypeException;
import bit.salesservice.exceptions.invaliddurationdays.InvalidDurationDaysException;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.multiplecampaign.MultipleCampaignException;
import bit.salesservice.exceptions.nullcampaignname.NullDiscountAmountException;
import bit.salesservice.exceptions.nulldiscountamount.NullCampaignNameException;
import bit.salesservice.repository.CampaignRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignValidator {
    public void validateCampaignDTO(CampaignDTO campaignDTO, CampaignRepository campaignRepository) {
        checkIfDiscountAmountNull(campaignDTO.getDiscountAmount());
        checkIfDurationDaysNull(campaignDTO.getDurationDays());
        validateProductCodes(campaignDTO.getCodes(), campaignRepository);
        validateDurationDays(campaignDTO);
        validateCampaignName(campaignDTO.getName());
        validateDiscountAmount(campaignDTO.getDiscountAmount());
        validateDiscountType(campaignDTO.getDiscountType());
        validateNeededQuantity(campaignDTO.getNeededQuantity());
    }

    public void validateDurationDays(CampaignDTO campaignDTO) {
        Integer durationDays = campaignDTO.getDurationDays();

        if (durationDays <= 0) {
            throw new InvalidDurationDaysException("Duration of the campaign must be a positive integer");
        }

        if (durationDays > 360){
            throw new InvalidDurationDaysException("Duration of the campaign cannot be more than 1 year");
        }
    }

    public void validateProductCodes(List<String> codes, CampaignRepository campaignRepository) {
        for (String code : codes) {
            List<Campaign> campaignList = campaignRepository.findByCodesContaining(code);
            if (!campaignList.isEmpty()) {
                throw new MultipleCampaignException("A product can only have one campaign: " + code);
            }
        }
    }

    public void validateDiscountAmount(Double amount) {
        if (amount <= 0) {
            throw new InvalidDiscountAmountException("Discount amount cannot be 0 or negative");
        }
    }

    public void validateDiscountType(String discountType) {
        if (discountType.isEmpty()) {
            throw new InvalidDiscountTypeException("No discount type provided");
        }
    }

    public void validateNeededQuantity(Integer quantity) {
        if (quantity != null && (quantity <= 0)) {
                throw new InvalidQuantityException("Needed quantity cannot be 0 or negative");
        }
    }

    private void checkIfDiscountAmountNull(Double amount){
        if (amount == null) {
            throw new NullDiscountAmountException("No discount amount provided");
        }
    }

    private void validateCampaignName(String name) {
        if (name.isEmpty()) {
            throw new NullCampaignNameException("No campaign name provided");
        }
    }

    private static void checkIfDurationDaysNull(Integer durationDays) {
        if (durationDays == null) {
            throw new InvalidDurationDaysException("Duration of the campaign is not provided");
        }
    }

}