package bit.reportingservice.service.serviceimpl;

import bit.reportingservice.dto.ListProductReq;
import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.*;
import bit.reportingservice.entity.Campaign;
import bit.reportingservice.entity.PaymentMethod;
import bit.reportingservice.entity.Product;
import bit.reportingservice.entity.SaleReport;
import bit.reportingservice.exceptions.invalidfilter.InvalidFilterException;
import bit.reportingservice.exceptions.invalidpaymentmethod.InvalidPaymentMethodException;
import bit.reportingservice.exceptions.invalidsort.InvalidSortException;
import bit.reportingservice.exceptions.productnotfound.ProductNotFoundException;
import bit.reportingservice.exceptions.reportnotfound.ReportNotFoundException;
import bit.reportingservice.repository.CampaignRepository;
import bit.reportingservice.repository.ProductRepository;
import bit.reportingservice.repository.SaleReportRepository;
import bit.reportingservice.service.ReportingService;
import bit.reportingservice.utils.ReceiptGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides the implementation for the ReportingService interface.
 * It handles the operations related to sale reports, products, campaigns, and receipts.
 */
@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {
    private final SaleReportRepository saleReportRepository;
    private final ProductRepository productRepository;
    private final CampaignRepository campaignRepository;
    private final ReceiptGenerator receiptGenerator;
    private static final Logger logger = LoggerFactory.getLogger(ReportingServiceImpl.class);

    /**
     * This method saves a sale report. It maps the provided SaleReportDTO to a SaleReport object and saves it using the saleReportRepository.
     *
     * @param saleReportDTO The SaleReportDTO containing the details of the sale report to be saved.
     */
    @Override
    public void saveSaleReport(SaleReportDTO saleReportDTO) {
        logger.info("Saving sale report...");

        SaleReport saleReport = mapToSaleReport(saleReportDTO);

        saleReportRepository.save(saleReport);

        logger.info("Sale report saved");
    }

    /**
     * This method saves a cancelled state of a sale report. It retrieves the sale report by its ID, sets its cancelled status, cancelled date, returned money, and total price to 0, and saves it using the saleReportRepository.
     *
     * @param cancelledSaleReportDTO The CancelledSaleReportDTO containing the details of the sale report to be cancelled.
     */
    @Override
    public void saveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO) {
        logger.info("Saving cancelled sale report...");

        SaleReport saleReport = saleReportRepository.findById(cancelledSaleReportDTO.getId())
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        saleReport.setCancelled(true);
        saleReport.setCancelledDate(cancelledSaleReportDTO.getCanceledDate());
        saleReport.setReturnedMoney(cancelledSaleReportDTO.getReturnedMoney());
        saleReport.setTotalPrice(0D);

        for (Product product : saleReport.getProducts()) {
            product.setReturned(true);
            product.setReturnedQuantity(product.getQuantity());
            product.setQuantity(0);
        }

        saleReportRepository.save(saleReport);

        logger.info("Cancelled sale report saved");
    }

    /**
     * This method updates a product and its associated sale report. It retrieves the product by its ID, updates its returned status, quantity, returned quantity, and sale report's returned money and total price, and saves both the product and the sale report using the respective repositories.
     *
     * @param returnedProductInfoDTO The ReturnedProductInfoDTO containing the details of the product and sale report to be updated.
     */
    @Override
    public void updateProductAndSaleReport(ReturnedProductInfoDTO returnedProductInfoDTO) {
        logger.info("Updating product and sale report...");

        Product product = productRepository.findById(returnedProductInfoDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setReturned(returnedProductInfoDTO.getReturned());
        product.setQuantity(returnedProductInfoDTO.getQuantity());
        product.setReturnedQuantity(returnedProductInfoDTO.getReturnedQuantity());
        product.getSaleReport().setReturnedMoney(returnedProductInfoDTO.getReturnedMoney());
        product.getSaleReport().setTotalPrice(product.getSaleReport().getTotalPrice() - returnedProductInfoDTO.getReturnedMoney());

        productRepository.save(product);
        saleReportRepository.save(product.getSaleReport());

        logger.info("Product and sale report updated");
    }

    /**
     * This method lists reports based on the provided parameters. It retrieves the reports based on the specified page, size, sorting criteria, and filtering options, and maps the retrieved reports to ListReportsReq objects before returning them.
     *
     * @param page The page number to retrieve the reports from.
     * @param size The number of reports to retrieve per page.
     * @param sortBy The sorting criteria to apply to the reports.
     * @param filterBy The filtering options to apply to the reports.
     * @param paymentMethod The payment method to filter the reports by.
     * @return A list of ListReportsReq objects representing the retrieved reports, along with their associated products, total prices, payment methods, money taken, change, completed dates, cancelled dates, returned money, and cancellation status.
     */
    @Override
    public List<ListReportsReq> listReports(int page, int size, String sortBy, String filterBy, String paymentMethod) {
            logger.info("Listing reports...");

            Pageable pageable = PageRequest.of(page, size, getSort(sortBy));

            Page<SaleReport> saleReportsPage = filterReports(filterBy, getPaymentMethod(paymentMethod), pageable);

             logger.info("Reports listed");

            return saleReportsPage.map(this::mapToListReportReq).getContent();
        }

    /**
     * This method generates a PDF receipt for a given sale report. It retrieves the sale report by its ID, generates the PDF receipt using the receiptGenerator, and returns the generated PDF byte array.
     *
     * @param reportId The ID of the sale report for which the PDF receipt needs to be generated.
     * @return A byte array representing the generated PDF receipt.
     * @throws IOException If an error occurs while generating the PDF receipt.
     */
    @Override
    public byte[] generatePdfReceipt(Long reportId) throws IOException {
        logger.info("Generating pdf receipt...");

        SaleReport saleReport = saleReportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException("Report not found"));

        logger.info("Receipt generated");

        return receiptGenerator.generate(saleReport);
    }

    /**
     * This method saves a campaign. It retrieves the campaign by its name, and if it does not exist, creates a new campaign with the provided details. If the campaign already exists, it updates its needed quantity based on the provided details.
     *
     * @param campaignDTO The CampaignDTO containing the details of the campaign to be saved or updated.
     */
    @Override
    public void saveCampaign(CampaignDTO campaignDTO) {
        logger.info("Saving campaign...");

        Optional<Campaign> campaign = campaignRepository.findByName(campaignDTO.getName());

        if (campaign.isEmpty()){
            Campaign newCampaign = new Campaign();
            newCampaign.setName(campaignDTO.getName());
            newCampaign.setDiscountAmount(campaignDTO.getDiscountAmount());
            newCampaign.setDiscountType(campaignDTO.getDiscountType());
            newCampaign.setNeededQuantity(campaignDTO.getNeededQuantity());

            campaignRepository.save(newCampaign);
        }
        else{
            campaign.get().setNeededQuantity(campaignDTO.getNeededQuantity());
            campaign.get().setDiscountAmount(campaignDTO.getDiscountAmount());
            campaign.get().setDiscountType(campaignDTO.getDiscountType());

            campaignRepository.save(campaign.get());
        }

        logger.info("Campaign saved");
    }

    /**
     * This method filters reports based on the specified filtering options and payment method. It retrieves the reports based on the specified filtering options and payment method, and returns them as a Page object along with the provided Pageable object.
     *
     * @param filterBy The filtering options to apply to the reports.
     * @param paymentMethod The payment method to filter the reports by.
     * @param pageable The Pageable object containing the page number, size, and sorting criteria to apply to the reports.
     * @return A Page object containing the filtered reports along with the provided Pageable object.
     */
    private Page<SaleReport> filterReports(String filterBy, PaymentMethod paymentMethod, Pageable pageable) {
        if (filterBy == null) {
            return saleReportRepository.findAll(pageable);
        }

        return switch (filterBy) {
            case "CANCELLED_ONLY" -> saleReportRepository.findByCancelled(true, pageable);
            case "PAYMENT_METHOD" -> saleReportRepository.findByPaymentMethod(paymentMethod, pageable);
            default -> throw new InvalidFilterException("Unexpected value: " + filterBy);
        };
    }

    /**
     * This method gets the sorting criteria based on the provided sorting option.
     *
     * @param sortBy The sorting option to apply to the reports.
     * @return The Sort object containing the sorting criteria based on the provided sorting option.
     * @throws InvalidSortException If the provided sorting option is unexpected.
     */
    private Sort getSort(String sortBy) {
        return switch (sortBy) {
            case "COMPLETED_DATE_DESC" -> Sort.by(Sort.Direction.DESC, "completedDate");
            case "TOTAL_PRICE_DESC" -> Sort.by(Sort.Direction.DESC, "totalPrice");
            default -> throw new InvalidSortException("Unexpected value: " + sortBy);
        };
    }

    /**
     * Maps a SaleReport object to a ListReportsReq object.
     *
     * @param saleReport The SaleReport object to be mapped.
     * @return A ListReportsReq object containing the mapped data.
     */
    private ListReportsReq mapToListReportReq(SaleReport saleReport) {
        return new ListReportsReq(
                saleReport.getProducts().stream()
                        .map(this::mapToListProductReq)
                        .toList(),
                saleReport.getTotalPrice(),
                saleReport.getPaymentMethod(),
                saleReport.getMoneyTaken(),
                saleReport.getChange(),
                saleReport.getCompletedDate(),
                saleReport.getCancelledDate(),
                saleReport.getReturnedMoney(),
                saleReport.isCancelled()
        );
    }

    /**
     * Maps a Product object to a ListProductReq object.
     *
     * @param product The Product object to be mapped.
     * @return A ListProductReq object containing the mapped data.
     */
    private ListProductReq mapToListProductReq(Product product) {
        return new ListProductReq(
                product.getCode(),
                product.getName(),
                product.getAppliedCampaign(),
                product.getQuantity(),
                product.getReturnedQuantity(),
                product.isReturned(),
                product.getPrice()
        );
    }

    /**
     * Maps a SaleReportDTO to a SaleReport object and saves it using the saleReportRepository.
     *
     * @param saleReportDTO The SaleReportDTO containing the details of the sale report to be saved.
     */
    private SaleReport mapToSaleReport(SaleReportDTO saleReportDTO) {
        SaleReport saleReport = new SaleReport();

        saleReport.setId(saleReportDTO.getId());
        saleReport.setTotalPrice(saleReportDTO.getTotalPrice());
        saleReport.setPaymentMethod(saleReportDTO.getPaymentMethod());
        saleReport.setMoneyTaken(saleReportDTO.getMoneyTaken());
        saleReport.setChange(saleReportDTO.getChange());
        saleReport.setCompletedDate(saleReportDTO.getCompletedDate());
        saleReport.setCancelledDate(saleReportDTO.getCancelledDate());
        saleReport.setReturnedMoney(saleReportDTO.getReturnedMoney());
        saleReport.setCancelled(saleReportDTO.isCancelled());

        List<Product> products = new ArrayList<>();

        for (ProductDTO productDTO : saleReportDTO.getProducts()){
            Product product = getProduct(productDTO, saleReport);

            products.add(product);
        }

        saleReport.setProducts(products);

        return saleReport;
    }

    /**
     * This method retrieves a product based on the provided product details and the associated sale report.
     *
     * @param productDTO The ProductDTO containing the details of the product to be retrieved.
     * @param saleReport The SaleReport object associated with the product.
     * @return A Product object containing the mapped data.
     * @throws InvalidPaymentMethodException If the provided payment method is unexpected.
     */
    private static Product getProduct(ProductDTO productDTO, SaleReport saleReport) {
        Product product = new Product();

        product.setId(productDTO.getId());
        product.setCode(productDTO.getCode());
        product.setName(productDTO.getName());
        product.setAppliedCampaign(productDTO.getAppliedCampaign());
        product.setQuantity(productDTO.getQuantity());
        product.setReturnedQuantity(productDTO.getReturnedQuantity());
        product.setSaleReport(saleReport);
        product.setPrice(productDTO.getPrice());
        product.setReturned(productDTO.isReturned());
        return product;
    }

    /**
     * This method retrieves a payment method based on the provided string.
     * It checks if the provided string is null, and if so, it returns null.
     * If the provided string is not null, it attempts to convert it to a PaymentMethod enum value using the {@code PaymentMethod.valueOf(String)} method.
     * If the conversion is successful, it returns the corresponding PaymentMethod enum value.
     * If the conversion fails due to an IllegalArgumentException, it throws an {@code InvalidPaymentMethodException} with the message "Invalid payment method".
     * @param payment The string representing the payment method to be retrieved.
     * @return The PaymentMethod enum value corresponding to the provided string, or null if the provided string is null.
     * @throws InvalidPaymentMethodException If the conversion fails due to an IllegalArgumentException.
     */
    private static PaymentMethod getPaymentMethod(String payment){
        if (payment == null) {
            return null;
        }

        try {
            return PaymentMethod.valueOf(payment);
        }
        catch (IllegalArgumentException e){
            throw new InvalidPaymentMethodException("Invalid payment method");
        }
    }
}
