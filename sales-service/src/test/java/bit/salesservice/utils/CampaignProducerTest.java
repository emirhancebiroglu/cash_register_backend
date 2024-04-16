package bit.salesservice.utils;

import bit.salesservice.dto.kafka.CampaignDTO;
import bit.salesservice.entity.DiscountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class CampaignProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CampaignProducer campaignProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendCampaign() {
        String topic = "test-topic";
        CampaignDTO campaignDTO = new CampaignDTO();
        campaignDTO.setName("Test Campaign");
        campaignDTO.setDiscountAmount(10.0);
        campaignDTO.setDiscountType(DiscountType.PERCENTAGE);

        campaignProducer.sendCampaign(topic, campaignDTO);
        verify(kafkaTemplate, times(1)).send(topic, campaignDTO);
    }
}