package bit.reportingservice.service;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.CampaignDTO;
import bit.reportingservice.dto.kafka.CancelledSaleReportDTO;
import bit.reportingservice.dto.kafka.ReturnedProductInfoDTO;
import bit.reportingservice.dto.kafka.SaleReportDTO;

import java.io.IOException;
import java.util.List;

/**
 * Interface defining the contract for reporting services.
 */
public interface ReportingService {
    /**
     * Saves a sale report.
     *
     * @param saleReportDTO The DTO containing sale report information.
     */
    void saveSaleReport(SaleReportDTO saleReportDTO);

    /**
     * Saves the cancelled state of a sale report.
     *
     * @param cancelledSaleReportDTO The DTO containing cancelled sale report information.
     */
    void saveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO);

    /**
     * Updates product and sale report information based on returned product info.
     *
     * @param returnedProductInfoDTO The DTO containing returned product information.
     */
    void updateProductAndSaleReport(ReturnedProductInfoDTO returnedProductInfoDTO);

    /**
     * Retrieves a list of reports based on pagination, sorting, filtering, and payment method.
     *
     * @param page          The page number.
     * @param size          The page size.
     * @param sortBy        The field to sort by.
     * @param filter        The filter criteria.
     * @param paymentMethod The payment method.
     * @return A list of report DTOs.
     */
    List<ListReportsReq> listReports(int page, int size, String sortBy, String filter, String paymentMethod);

    /**
     * Generates a PDF receipt for the specified sale report.
     *
     * @param reportId The ID of the sale report.
     * @return The PDF byte array.
     * @throws IOException If an I/O error occurs.
     */
    byte[] generatePdfReceipt(Long reportId) throws IOException;

    /**
     * Saves campaign information.
     *
     * @param campaignDTO The DTO containing campaign information.
     */
    void saveCampaign(CampaignDTO campaignDTO);
}
