package bit.salesservice.validators;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.exceptions.activecampaign.ActiveCampaignException;
import bit.salesservice.exceptions.campaignalreadyexists.CampaignAlreadyExistsException;
import bit.salesservice.exceptions.inactivecampaign.InactiveCampaignException;
import bit.salesservice.exceptions.invaliddiscountamount.InvalidDiscountAmountException;
import bit.salesservice.exceptions.invaliddiscounttype.InvalidDiscountTypeException;
import bit.salesservice.exceptions.invaliddurationdays.InvalidDurationDaysException;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.invalidstatustype.InvalidStatusTypeException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CampaignValidatorTest {
    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignValidator campaignValidator;

    private AddAndUpdateCampaignReq request;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new AddAndUpdateCampaignReq();
        request.setName("Test Campaign");
        request.setDiscountAmount(10.0);
        request.setDurationDays(30);
        request.setDiscountType("Percentage");
        request.setCodes(Collections.singletonList("product1"));

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setInactive(true);
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
    void validateCampaignDTO_NotEmptyName() {
        request.setName("test name");
        when(campaignRepository.findByName(request.getName())).thenReturn(campaign);

        assertThrows(CampaignAlreadyExistsException.class, () -> campaignValidator.validateCampaignDTO(request, campaignRepository));
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

        assertThrows(MultipleCampaignException.class, this::validateProductCodes);
    }

    @Test
    void validateDiscountAmount_Success() {
        AddAndUpdateCampaignReq addAndUpdateCampaignReq = new AddAndUpdateCampaignReq();
        addAndUpdateCampaignReq.setDiscountAmount(10D);

        assertDoesNotThrow(() -> campaignValidator.validateDiscountAmount(addAndUpdateCampaignReq));
    }

    @Test
    void validateDiscountAmount_NegativeAmount() {
        AddAndUpdateCampaignReq addAndUpdateCampaignReq = new AddAndUpdateCampaignReq();
        addAndUpdateCampaignReq.setDiscountAmount(-10D);

        assertThrows(InvalidDiscountAmountException.class, () -> campaignValidator.validateDiscountAmount(addAndUpdateCampaignReq));
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

    @Test
    void validateActivation_InactiveCampaign() {
        // Invoke validateActivation with an inactive campaign
        assertThrows(InactiveCampaignException.class, () -> campaignValidator.validateActivation(campaign));
    }

    @Test
    void validateActivation_ActiveCampaign() {
        // Invoke validateActivation with an active campaign
        campaign.setInactive(false);
        assertDoesNotThrow(() -> campaignValidator.validateActivation(campaign));
    }

    @Test
    void validateInactivation_InactiveCampaign() {
        // Invoke validateInactivation with an inactive campaign
        assertDoesNotThrow(() -> campaignValidator.validateInactivation(campaign));
    }

    @Test
    void validateInactivation_ActiveCampaign() {
        // Invoke validateInactivation with an active campaign
        campaign.setInactive(false);
        assertThrows(ActiveCampaignException.class, () -> campaignValidator.validateInactivation(campaign));
    }

    @Test
    void validateDiscountTypeAndStatus_ValidTypes() {
        // Act
        DiscountType discountType = campaignValidator.validateDiscountTypeAndStatus("percentage", "active");

        // Assert
        assertEquals(DiscountType.PERCENTAGE, discountType);
    }

    @Test
    void validateDiscountTypeAndStatus_InvalidDiscountType() {
        // Act & Assert
        assertThrows(InvalidDiscountTypeException.class, () ->
                campaignValidator.validateDiscountTypeAndStatus("invalidType", "active"));
    }

    @Test
    void validateDiscountTypeAndStatus_InvalidStatusType() {
        // Act & Assert
        assertThrows(InvalidStatusTypeException.class, () ->
                campaignValidator.validateDiscountTypeAndStatus("percentage", "invalidStatus"));
    }

    @Test
    void validateDiscountTypeAndStatus_InvalidTypes() {
        // Act & Assert
        assertThrows(InvalidDiscountTypeException.class, () ->
                campaignValidator.validateDiscountTypeAndStatus("invalidType", "invalidStatus"));
    }

    @Test
    void validateDiscountTypeAndStatus_NullDiscountType() {
        // Act
        DiscountType discountType = campaignValidator.validateDiscountTypeAndStatus(null, "active");

        // Assert
        assertNull(discountType);
    }

    @Test
    void validateDiscountTypeAndStatus_NullStatus() {
        // Act
        DiscountType discountType = campaignValidator.validateDiscountTypeAndStatus("percentage", null);

        // Assert
        assertEquals(DiscountType.PERCENTAGE, discountType);
    }

    @Test
    void validateDiscountTypeAndStatus_NullTypes() {
        // Act
        DiscountType discountType = campaignValidator.validateDiscountTypeAndStatus(null, null);

        // Assert
        assertNull(discountType);
    }

    private void validateProductCodes() {
        campaignValidator.validateProductCodes(Collections.singletonList("product1"), campaignRepository);
    }
}
