package bit.reportingservice.utils;

import bit.reportingservice.dto.kafka.CancelledSaleReportDTO;
import bit.reportingservice.dto.kafka.ReturnedProductInfoDTO;
import bit.reportingservice.dto.kafka.SaleReportDTO;
import bit.reportingservice.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class SaleReportConsumerTest {
    @Mock
    private ReportingService reportingService;

    @InjectMocks
    private SaleReportConsumer saleReportConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveSaleReport_shouldSaveSaleReport() {
        SaleReportDTO saleReportDTO = new SaleReportDTO();

        saleReportConsumer.receiveSaleReport(saleReportDTO);

        verify(reportingService).saveSaleReport(saleReportDTO);
    }

    @Test
    void receiveCancelledStateOfSaleReport_shouldSaveCancelledStateOfSaleReport() {
        CancelledSaleReportDTO cancelledSaleReportDTO = new CancelledSaleReportDTO();

        saleReportConsumer.receiveCancelledStateOfSaleReport(cancelledSaleReportDTO);

        verify(reportingService).saveCancelledStateOfSaleReport(cancelledSaleReportDTO);
    }

    @Test
    void receiveReturnedProductInfo_shouldUpdateProductAndSaleReport() {
        ReturnedProductInfoDTO returnedProductInfoDTO = new ReturnedProductInfoDTO();

        saleReportConsumer.receiveReturnedProductInfo(returnedProductInfoDTO);

        verify(reportingService).updateProductAndSaleReport(returnedProductInfoDTO);
    }
}
