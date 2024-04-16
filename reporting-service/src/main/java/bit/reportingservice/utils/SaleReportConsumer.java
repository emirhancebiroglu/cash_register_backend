package bit.reportingservice.utils;

import bit.reportingservice.dto.kafka.CancelledSaleReportDTO;
import bit.reportingservice.dto.kafka.ReturnedProductInfoDTO;
import bit.reportingservice.dto.kafka.SaleReportDTO;
import bit.reportingservice.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleReportConsumer {
    private final ReportingService reportingService;
    private static final Logger logger = LoggerFactory.getLogger(SaleReportConsumer.class);

    @KafkaListener(topics = "sale-report", groupId = "sales")
    public void receiveSaleReport(SaleReportDTO saleReportDTO){
        logger.info("Received Sale Report: {}", saleReportDTO);

        reportingService.saveSaleReport(saleReportDTO);
    }

    @KafkaListener(topics = "cancelled-sale-report", groupId = "sales")
    public void receiveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO){
        logger.info("Received Cancelled State of Sale Report");

        reportingService.saveCancelledStateOfSaleReport(cancelledSaleReportDTO);

    }

    @KafkaListener(topics = "returned-product-info", groupId = "sales")
    public void receiveReturnedProductInfo(ReturnedProductInfoDTO returnedProductInfoDTO){
        logger.info("Received Returned Product Info");

        reportingService.updateProductAndSaleReport(returnedProductInfoDTO);

    }
}
