package bit.reportingservice.service;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.CampaignDTO;
import bit.reportingservice.dto.kafka.CancelledSaleReportDTO;
import bit.reportingservice.dto.kafka.ReturnedProductInfoDTO;
import bit.reportingservice.dto.kafka.SaleReportDTO;

import java.io.IOException;
import java.util.List;

public interface ReportingService {
    void saveSaleReport(SaleReportDTO saleReportDTO);
    void saveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO);
    void updateProductAndSaleReport(ReturnedProductInfoDTO returnedProductInfoDTO);
    List<ListReportsReq> listReports(int page, int size, String sortBy, String filter, String paymentMethod);
    byte[] generatePdfReceipt(Long reportId) throws IOException;
    void saveCampaign(CampaignDTO campaignDTO);
}
