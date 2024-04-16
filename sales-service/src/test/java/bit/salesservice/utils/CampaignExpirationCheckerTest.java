package bit.salesservice.utils;

import bit.salesservice.entity.Campaign;
import bit.salesservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class CampaignExpirationCheckerTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignExpirationChecker campaignExpirationChecker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckAndUpdateCampaignStatus() {
        Campaign expiredCampaign = new Campaign();
        expiredCampaign.setId(1L);
        expiredCampaign.setEndDate(LocalDateTime.now().minusDays(1));

        Campaign activeCampaign = new Campaign();
        activeCampaign.setId(2L);
        activeCampaign.setEndDate(LocalDateTime.now().plusDays(1));

        List<Campaign> campaigns = new ArrayList<>();
        campaigns.add(expiredCampaign);
        campaigns.add(activeCampaign);

        when(campaignRepository.findAllByisInactiveIsFalse()).thenReturn(campaigns);

        campaignExpirationChecker.checkAndUpdateCampaignStatus();

        verify(campaignRepository, times(1)).save(expiredCampaign);
        verify(campaignRepository, never()).save(activeCampaign);
    }
}