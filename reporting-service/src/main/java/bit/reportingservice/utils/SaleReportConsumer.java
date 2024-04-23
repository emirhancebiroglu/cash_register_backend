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

/**
 * This class is a Kafka consumer that listens to three different Kafka topics:
 * "sale-report", "cancelled-sale-report", and "returned-product-info".
 * It consumes the messages from these topics and processes them accordingly.
 */
@Component
@RequiredArgsConstructor
public class SaleReportConsumer {

    /**
     * The ReportingService instance that is used to save and update reports.
     */
    private final ReportingService reportingService;

    /**
     * A static Logger instance that logs information about the received reports.
     */
    private static final Logger logger = LoggerFactory.getLogger(SaleReportConsumer.class);

    /**
     * This method is a Kafka listener that listens to the "sale-report" topic.
     * When a message is received, it logs the received SaleReportDTO and saves it using the reportingService.
     *
     * @param saleReportDTO The SaleReportDTO object that is received from the Kafka topic.
     */
    @KafkaListener(topics = "sale-report", groupId = "sales")
    public void receiveSaleReport(SaleReportDTO saleReportDTO){
        logger.info("Received Sale Report: {}", saleReportDTO);

        reportingService.saveSaleReport(saleReportDTO);
    }

    /**
     * This method is a Kafka listener that listens to the "cancelled-sale-report" topic.
     * When a message is received, it logs the received CancelledSaleReportDTO and saves it using the reportingService.
     *
     * @param cancelledSaleReportDTO The CancelledSaleReportDTO object that is received from the Kafka topic.
     */
    @KafkaListener(topics = "cancelled-sale-report", groupId = "sales")
    public void receiveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO){
        logger.info("Received Cancelled State of Sale Report");

        reportingService.saveCancelledStateOfSaleReport(cancelledSaleReportDTO);

    }

    /**
     * This method is a Kafka listener that listens to the "returned-product-info" topic.
     * When a message is received, it logs the received ReturnedProductInfoDTO and updates the product and sale report using the reportingService.
     *
     * @param returnedProductInfoDTO The ReturnedProductInfoDTO object that is received from the Kafka topic.
     */
    @KafkaListener(topics = "returned-product-info", groupId = "sales")
    public void receiveReturnedProductInfo(ReturnedProductInfoDTO returnedProductInfoDTO){
        logger.info("Received Returned Product Info");

        reportingService.updateProductAndSaleReport(returnedProductInfoDTO);

    }
}
