package bit.salesservice.utils;

import bit.salesservice.dto.kafka.CancelledSaleReportDTO;
import bit.salesservice.dto.kafka.ReturnedProductInfoDTO;
import bit.salesservice.dto.kafka.SaleReportDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaleReportProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SaleReportProducer.class);

    public void sendSaleReport(String topic, SaleReportDTO saleReportDTO){
        kafkaTemplate.send(topic, saleReportDTO);
        logger.info("Sale report sent to kafka topic: {}", topic);
    }

    public void sendCancelledSaleReport(String topic, CancelledSaleReportDTO cancelledSaleReportDTO){
        kafkaTemplate.send(topic, cancelledSaleReportDTO);
        logger.info("Cancelled sale report sent to kafka topic: {}", topic);
    }

    public void sendReturnedProductInfoToReportingService(String topic, ReturnedProductInfoDTO returnedProductInfoDTO){
        kafkaTemplate.send(topic, returnedProductInfoDTO);
        logger.info("Returned product info sent to kafka topic: {}", topic);
    }
}
