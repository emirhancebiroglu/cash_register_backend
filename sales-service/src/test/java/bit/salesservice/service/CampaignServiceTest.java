package bit.salesservice.service;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.entity.Campaign;
import bit.salesservice.exceptions.activecampaign.ActiveCampaignException;
import bit.salesservice.exceptions.campaignalreadyexists.CampaignAlreadyExistsException;
import bit.salesservice.exceptions.campaignnotfound.CampaignNotFoundException;
import bit.salesservice.exceptions.fixedamountdiscounttypewithprovidedquantity.FixedAmountDiscountTypeWithProvidedQuantityException;
import bit.salesservice.exceptions.inactivecampaign.InactiveCampaignException;
import bit.salesservice.exceptions.invaliddiscounttype.InvalidDiscountTypeException;
import bit.salesservice.exceptions.invalidstatustype.InvalidStatusTypeException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.service.serviceimpl.CampaignServiceImpl;
import bit.salesservice.utils.CampaignProducer;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.validators.CampaignValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ProductInfoHttpRequest productInfoHttpRequest;

    @Mock
    private CampaignValidator campaignValidator;

    @Mock
    private CampaignProducer campaignProducer;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private AddAndUpdateCampaignReq request;
    private Campaign campaign;
    private Long campaignId;
    private Mono<ProductInfo> mono;
    private List<Campaign> campaigns;

    @BeforeEach
    public void setUp() {
        campaignService = new CampaignServiceImpl(campaignRepository, productInfoHttpRequest, campaignValidator, campaignProducer);

        request = new AddAndUpdateCampaignReq();
        request.setName("Test Campaign");
        request.setCodes(List.of("code1", "code2"));
        request.setDiscountType("PERCENTAGE");
        request.setDiscountAmount(10D);
        request.setDurationDays(7);

        campaigns = new ArrayList<>();

        campaign = new Campaign();
        campaignId = 1L;
        campaign.setId(campaignId);
        campaign.setStartDate(LocalDateTime.now().minusDays(7));
        campaign.setEndDate(LocalDateTime.now().plusDays(7));

        ProductInfo productInfo = new ProductInfo();
        productInfo.setExists(true);
        mono = Mono.just(productInfo);
    }

    @Test
    void addCampaign_Success() {
        Campaign campaign = new Campaign();
        campaign.setName("Test Campaign");

        when(campaignRepository.findByName(request.getName())).thenReturn(null);
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(mono.block());
        when(campaignRepository.save(any())).thenReturn(campaign);

        assertDoesNotThrow(() -> campaignService.addCampaign(request));
    }

    @Test
    void addCampaign_WithExistingCampaign_ThrowsCampaignAlreadyExistsException() {
        when(campaignRepository.findByName(request.getName())).thenReturn(new Campaign());

        assertThrows(CampaignAlreadyExistsException.class, () -> campaignService.addCampaign(request));
    }

    @Test
    void addCampaign_WithNonExistingProduct_ThrowsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> campaignService.addCampaign(request));
    }

    @Test
    void addCampaign_WithProvidedQuantityForFixedAmountDiscountType_ThrowsFixedAmountDiscountTypeWithProvidedQuantityException() {
        request.setNeededQuantity(3);
        request.setDiscountType("FIXED_AMOUNT");

        Campaign campaign = new Campaign();
        campaign.setName("Test Campaign");

        when(campaignRepository.findByName(request.getName())).thenReturn(null);
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(mono.block());

        assertThrows(FixedAmountDiscountTypeWithProvidedQuantityException.class, () -> campaignService.addCampaign(request));
    }

    @Test
    void addCampaign_WithFixedAmountDiscountType() {
        request.setDiscountType("FIXED_AMOUNT");
        request.setNeededQuantity(1);

        Campaign campaign = new Campaign();
        campaign.setName("Test Campaign");

        when(campaignRepository.findByName(request.getName())).thenReturn(null);
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(mono.block());

        assertDoesNotThrow(() -> campaignService.addCampaign(request));
    }

    @Test
    void inactivateCampaign_Success() {
        campaign.setInactive(false);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        campaignService.inactivateCampaign(campaignId);

        assertTrue(campaign.isInactive());

        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void inactivateCampaign_CampaignNotFound() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class, () -> campaignService.inactivateCampaign(campaignId));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void inactivateCampaign_AlreadyInactive() {
        campaign.setInactive(true);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        assertThrows(InactiveCampaignException.class, () -> campaignService.inactivateCampaign(campaignId));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void reactivateCampaign_Success() {
        Integer durationDays = 7;
        campaign.setInactive(true);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        campaignService.reactivateCampaign(campaignId, durationDays);

        assertFalse(campaign.isInactive());

        assertEquals(durationDays, campaign.getDurationDays());
        assertNotNull(campaign.getStartDate());
        assertNotNull(campaign.getUpdatedDate());

        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void reactivateCampaign_CampaignNotFound() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class, () -> campaignService.reactivateCampaign(campaignId, 7));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void reactivateCampaign_AlreadyActive() {
        campaign.setInactive(false);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        assertThrows(ActiveCampaignException.class, () -> campaignService.reactivateCampaign(campaignId, 7));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void updateCampaign_Success() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(mono.block());
        doNothing().when(campaignValidator).validateProductCodes(any(), any());
        when(campaignRepository.save(any())).thenReturn(campaign);

        campaignService.updateCampaign(request, campaignId);

        assertEquals(request.getName(), campaign.getName());
        assertEquals(request.getCodes(), campaign.getCodes());
        assertEquals(request.getDiscountType(), campaign.getDiscountType().toString());
        assertEquals(request.getDiscountAmount(), campaign.getDiscountAmount());
        assertEquals(request.getDurationDays(), campaign.getDurationDays());
        assertEquals(campaign.getStartDate().plusDays(request.getDurationDays()), campaign.getEndDate());
        assertNotNull(campaign.getUpdatedDate());

        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void updateCampaign_WithNotNullNeededQuantity_Success() {
        request.setNeededQuantity(3);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(mono.block());
        doNothing().when(campaignValidator).validateProductCodes(any(), any());
        when(campaignRepository.save(any())).thenReturn(campaign);

        campaignService.updateCampaign(request, campaignId);

        assertEquals(request.getNeededQuantity(), campaign.getNeededQuantity());

        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void updateCampaign_WithInvalidDiscountType_ThrowsInvalidDiscountTypeException() {
        request.setDiscountType("Invalid");

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(mono.block());
        doNothing().when(campaignValidator).validateProductCodes(any(), any());

        assertThrows(InvalidDiscountTypeException.class, () -> campaignService.updateCampaign(request, campaignId));
    }

    @Test
    void updateCampaign_InactiveCampaign() {
        campaign.setInactive(true);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

        assertThrows(InactiveCampaignException.class, () -> campaignService.updateCampaign(request, campaignId));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void updateCampaign_ProductNotFound() {
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(productInfoHttpRequest.getProductInfo(any(), any())).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> campaignService.updateCampaign(request, campaignId));

        verify(campaignRepository, never()).save(any());
    }

    @Test
    void getCampaigns_SortedByNameAscending_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Campaign> pageContent = campaigns;
        PageImpl<Campaign> page = new PageImpl<>(pageContent, pageable, pageContent.size());
        when(campaignRepository.findAll(pageable)).thenReturn(page);

        List<ListCampaignsReq> result = campaignService.getCampaigns(0, 10, null, null, null, "name", "ASC");

        assertEquals(campaigns.size(), result.size());
    }

    @Test
    void getCampaigns_InvalidStatusType_ThrowsInvalidStatusTypeException() {
        assertThrows(InvalidStatusTypeException.class, () -> campaignService.getCampaigns(0, 10, null, "invalid", null, "name", "DESC"));
    }
}
