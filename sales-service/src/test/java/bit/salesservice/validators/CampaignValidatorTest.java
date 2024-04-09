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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CampaignValidatorTest {
    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignValidator campaignValidator;

    private AddAndUpdateCampaignReq request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new AddAndUpdateCampaignReq();
        request.setName("Test Campaign");
        request.setDiscountAmount(10.0);
        request.setDurationDays(30);
        request.setDiscountType("Percentage");
        request.setCodes(Collections.singletonList("product1"));
    }

    @Test
    void validateCampaignDTO_Success() {
        when(campaignRepository.findByCodesContaining("product1")).thenReturn(Collections.emptyList());

        campaignValidator.validateCampaignDTO(request, campaignRepository);
    }

    @Test
    void validateCampaignDTO_EmptyName() {
        request.setName("");
        when(campaignRepository.findByCodesContaining("product1")).thenReturn(Collections.emptyList());

        assertThrows(NullCampaignNameException.class, () -> campaignValidator.validateCampaignDTO(request, campaignRepository));
    }

    @Test
    void validateCampaignDTO_NullDurationDays() {
        request.setDurationDays(null);
        when(campaignRepository.findByCodesContaining("product1")).thenReturn(Collections.emptyList());

        assertThrows(InvalidDurationDaysException.class, () -> campaignValidator.validateCampaignDTO(request, campaignRepository));
    }

    @Test
    void validateCampaignDTO_NullDiscountAmount() {
        request.setDiscountAmount(null);

        assertThrows(InvalidDiscountAmountException.class, () ->
                campaignValidator.validateCampaignDTO(request, campaignRepository));
    }

    @Test
    void validateDurationDays_Success() {
        assertDoesNotThrow(() -> campaignValidator.validateDurationDays(request));
    }

    @Test
    void validateDurationDays_NegativeDuration() {
        request.setDurationDays(-10);

        assertThrows(InvalidDurationDaysException.class, () -> campaignValidator.validateDurationDays(request));
    }

    @Test
    void validateDurationDays_LongDuration() {
        request.setDurationDays(400);

        assertThrows(InvalidDurationDaysException.class, () -> campaignValidator.validateDurationDays(request));
    }

    @Test
    void validateProductCodes_NoMultipleCampaign() {
        when(campaignRepository.findByCodesContaining(anyString())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> campaignValidator.validateProductCodes(Collections.singletonList("product1"), campaignRepository));
    }

    @Test
    void validateProductCodes_MultipleCampaigns() {
        when(campaignRepository.findByCodesContaining(anyString())).thenReturn(List.of(new Campaign(), new Campaign()));

        assertThrows(MultipleCampaignException.class, () -> campaignValidator.validateProductCodes(Collections.singletonList("product1"), campaignRepository));
    }

    @Test
    void validateDiscountAmount_Success() {
        assertDoesNotThrow(() -> campaignValidator.validateDiscountAmount(10.0));
    }

    @Test
    void validateDiscountAmount_NegativeAmount() {
        assertThrows(InvalidDiscountAmountException.class, () -> campaignValidator.validateDiscountAmount(-10.0));
    }

    @Test
    void validateDiscountType_Success() {
        assertDoesNotThrow(() -> campaignValidator.validateDiscountType("Percentage"));
    }

    @Test
    void validateDiscountType_EmptyType() {
        assertThrows(InvalidDiscountTypeException.class, () -> campaignValidator.validateDiscountType(""));
    }

    @Test
    void validateNeededQuantity_Success() {
        assertDoesNotThrow(() -> campaignValidator.validateNeededQuantity(5));
    }

    @Test
    void validateNeededQuantity_NullQuantity() {
        assertDoesNotThrow(() -> campaignValidator.validateNeededQuantity(null));
    }

    @Test
    void validateNeededQuantity_NegativeQuantity() {
        assertThrows(InvalidQuantityException.class, () -> campaignValidator.validateNeededQuantity(-5));
    }

    @Test
    void validateNeededQuantity_ZeroQuantity() {
        assertThrows(InvalidQuantityException.class, () -> campaignValidator.validateNeededQuantity(0));
    }
}
