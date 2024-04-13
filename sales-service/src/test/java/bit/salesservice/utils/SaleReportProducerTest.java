package bit.salesservice.utils;

import bit.salesservice.dto.kafka.CancelledSaleReportDTO;
import bit.salesservice.dto.kafka.ReturnedProductInfoDTO;
import bit.salesservice.dto.kafka.SaleReportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SaleReportProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private SaleReportProducer saleReportProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendSaleReport() {
        String topic = "sale-report";
        SaleReportDTO saleReportDTO = new SaleReportDTO();

        saleReportProducer.sendSaleReport(topic, saleReportDTO);

        verify(kafkaTemplate, times(1)).send(topic, saleReportDTO);
    }

    @Test
    void sendCancelledSaleReport() {
        String topic = "cancelled-sale-report";
        CancelledSaleReportDTO cancelledSaleReportDTO = new CancelledSaleReportDTO();

        saleReportProducer.sendCancelledSaleReport(topic, cancelledSaleReportDTO);

        verify(kafkaTemplate, times(1)).send(topic, cancelledSaleReportDTO);
    }

    @Test
    void sendReturnedProductInfoToReportingService() {
        String topic = "returned-product-info";
        ReturnedProductInfoDTO returnedProductInfoDTO = new ReturnedProductInfoDTO();

        saleReportProducer.sendReturnedProductInfoToReportingService(topic, returnedProductInfoDTO);

        verify(kafkaTemplate, times(1)).send(topic, returnedProductInfoDTO);
    }
}
