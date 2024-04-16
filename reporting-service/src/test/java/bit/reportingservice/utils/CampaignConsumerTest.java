package bit.reportingservice.utils;

import bit.reportingservice.dto.kafka.CampaignDTO;
import bit.reportingservice.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CampaignConsumerTest {
    @Mock
    ReportingService reportingService;

    @InjectMocks
    CampaignConsumer campaignConsumer;

    private CampaignDTO campaignDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        campaignDTO = new CampaignDTO();
    }

    @Test
    void receiveCampaign_shouldSaveCampaign() {
        campaignConsumer.receiveCampaign(campaignDTO);

        verify(reportingService).saveCampaign(campaignDTO);
    }
}
