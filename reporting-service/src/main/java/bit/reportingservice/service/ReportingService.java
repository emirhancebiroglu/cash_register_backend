package bit.reportingservice.service;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.CancelledSaleReportDTO;
import bit.reportingservice.dto.kafka.ReturnedProductInfoDTO;
import bit.reportingservice.dto.kafka.SaleReportDTO;
import bit.reportingservice.entity.FilterBy;
import bit.reportingservice.entity.PaymentMethod;
import bit.reportingservice.entity.SortBy;

import java.util.List;

public interface ReportingService {
    void saveSaleReport(SaleReportDTO saleReportDTO);
    void saveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO);
    void updateProductAndSaleReport(ReturnedProductInfoDTO returnedProductInfoDTO);
    List<ListReportsReq> listReports(int page, int size, SortBy sortBy, FilterBy filter, PaymentMethod paymentMethod);
}
