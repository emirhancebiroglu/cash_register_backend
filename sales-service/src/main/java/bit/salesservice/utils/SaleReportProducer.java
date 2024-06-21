package bit.salesservice.utils;

import bit.salesservice.dto.kafka.CancelledSaleReportDTO;
import bit.salesservice.dto.kafka.ReturnedProductInfoDTO;
import bit.salesservice.dto.kafka.SaleReportDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for producing sale reports and related information to Kafka topics.
 */
@Component
@AllArgsConstructor
public class SaleReportProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LogManager.getLogger(SaleReportProducer.class);

    /**
     * Sends a sale report to the specified Kafka topic.
     *
     * @param topic         The Kafka topic to which the sale report will be sent.
     * @param saleReportDTO The sale report data to be sent.
     */
    public void sendSaleReport(String topic, SaleReportDTO saleReportDTO){
        kafkaTemplate.send(topic, saleReportDTO);
        logger.trace("Sale report sent to kafka topic: {}", topic);
    }

    /**
     * Sends a cancelled sale report to the specified Kafka topic.
     *
     * @param topic                    The Kafka topic to which the cancelled sale report will be sent.
     * @param cancelledSaleReportDTO The cancelled sale report data to be sent.
     */
    public void sendCancelledSaleReport(String topic, CancelledSaleReportDTO cancelledSaleReportDTO){
        kafkaTemplate.send(topic, cancelledSaleReportDTO);
        logger.trace("Cancelled sale report sent to kafka topic: {}", topic);
    }

    /**
     * Sends information about returned products to the specified Kafka topic.
     *
     * @param topic                   The Kafka topic to which the returned product info will be sent.
     * @param returnedProductInfoDTO The returned product information to be sent.
     */
    public void sendReturnedProductInfoToReportingService(String topic, ReturnedProductInfoDTO returnedProductInfoDTO){
        kafkaTemplate.send(topic, returnedProductInfoDTO);
        logger.trace("Returned product info sent to kafka topic: {}", topic);
    }
}