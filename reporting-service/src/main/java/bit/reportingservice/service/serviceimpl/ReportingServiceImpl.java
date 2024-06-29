package bit.reportingservice.service.serviceimpl;

import bit.reportingservice.dto.ListProductReq;
import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.*;
import bit.reportingservice.entity.Campaign;
import bit.reportingservice.entity.PaymentMethod;
import bit.reportingservice.entity.Product;
import bit.reportingservice.entity.SaleReport;
import bit.reportingservice.exceptions.productnotfound.ProductNotFoundException;
import bit.reportingservice.exceptions.reportnotfound.ReportNotFoundException;
import bit.reportingservice.repository.CampaignRepository;
import bit.reportingservice.repository.ProductRepository;
import bit.reportingservice.repository.SaleReportRepository;
import bit.reportingservice.service.ReportingService;
import bit.reportingservice.utils.ReceiptGenerator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private static final Logger logger = LogManager.getLogger(ReportingServiceImpl.class);
    private static final String CANCELLED = "cancelled";

    @Override
    public void saveSaleReport(SaleReportDTO saleReportDTO) {
        logger.trace("Saving sale report...");

        // Map the SaleReportDTO object to a SaleReport entity.
        SaleReport saleReport = mapToSaleReport(saleReportDTO);
        logger.debug("Mapped SaleReport entity: {}", saleReport);

        saleReportRepository.save(saleReport);
        logger.debug("SaleReport entity saved with ID: {}", saleReport.getId());

        logger.trace("Sale report saved");
    }

    @Override
    public void saveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO) {
        logger.trace("Saving cancelled sale report...");

        SaleReport saleReport = saleReportRepository.findById(cancelledSaleReportDTO.getId())
                .orElseThrow(() -> {
                    logger.error("Report not found for id {}", cancelledSaleReportDTO.getId());
                    return new ReportNotFoundException("Report not found");
                });
        logger.debug("SaleReport entity found: {}", saleReport);

        // Update the SaleReport entity with the cancelled status and related details.
        saleReport.setCancelled(true);
        saleReport.setCancelledDate(cancelledSaleReportDTO.getCanceledDate());
        saleReport.setReturnedMoney(cancelledSaleReportDTO.getReturnedMoney());
        logger.debug("Updated SaleReport entity with cancelled status: {}", saleReport);

        // Iterate through each product in the sale report to update its status.
        for (Product product : saleReport.getProducts()) {
            product.setReturned(true);
            product.setReturnedQuantity(product.getQuantity());
            logger.debug("Updated Product entity: {}", product);
        }

        saleReportRepository.save(saleReport);
        logger.debug("Cancelled SaleReport entity saved with ID: {}", saleReport.getId());

        logger.trace("Cancelled sale report saved");
    }

    @Override
    public void updateProductAndSaleReport(ReturnedProductInfoDTO returnedProductInfoDTO) {
        logger.trace("Updating product and sale report...");

        Product product = productRepository.findById(returnedProductInfoDTO.getId())
                .orElseThrow(() -> {
                    logger.error("Product not found for id {}", returnedProductInfoDTO.getId());
                    return new ProductNotFoundException("Product not found");
                });
        logger.debug("Product entity found: {}", product);

        // Update the Product entity with the returned product information.
        product.setReturned(returnedProductInfoDTO.getReturned());
        product.setQuantity(returnedProductInfoDTO.getQuantity());
        product.setReturnedQuantity(returnedProductInfoDTO.getReturnedQuantity());
        logger.debug("Updated Product entity: {}", product);

        // Update the SaleReport entity associated with the Product.
        product.getSaleReport().setReturnedMoney(returnedProductInfoDTO.getReturnedMoney());
        product.getSaleReport().setTotalPrice(product.getSaleReport().getTotalPrice() - returnedProductInfoDTO.getReturnedMoney());
        logger.debug("Updated SaleReport entity in Product: {}", product.getSaleReport());

        productRepository.save(product);
        logger.debug("Product entity saved: {}", product);
        saleReportRepository.save(product.getSaleReport());
        logger.debug("SaleReport entity saved: {}", product.getSaleReport());

        logger.trace("Product and sale report updated");
    }

    @Override
    public List<ListReportsReq> listReports(int page, int size, String cancelledStatus, String paymentStatus, String sortBy, String sortOrder) {
        logger.trace("Listing reports...");

        // Apply pagination and sorting parameters.
        Pageable pageable = applySort(page, size, sortBy, sortOrder);
        logger.debug("Constructed Pageable: {}", pageable);

        Page<SaleReport> saleReportsPage;

        // Define the specification for filtering sale reports.
        Specification<SaleReport> specification = Specification.where((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add predicate for payment status, if provided.
            if (paymentStatus != null) {
                if (paymentStatus.equalsIgnoreCase("cash")) {
                    predicates.add(criteriaBuilder.equal(root.get("paymentMethod"), PaymentMethod.CASH));
                    logger.debug("Added predicate for payment method: CASH");
                } else if (paymentStatus.equalsIgnoreCase("credit-card")) {
                    predicates.add(criteriaBuilder.equal(root.get("paymentMethod"), PaymentMethod.CREDIT_CARD));
                    logger.debug("Added predicate for payment method: CREDIT_CARD");
                }
            }
            // Add predicate for cancelled status, if provided.
            if (cancelledStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get(CANCELLED), cancelledStatus.equalsIgnoreCase(CANCELLED)));
                logger.debug("Added predicate for cancelled status: {}", cancelledStatus.equalsIgnoreCase(CANCELLED));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        // Retrieve the sale reports matching the specification and pageable parameters.
        saleReportsPage = saleReportRepository.findAll(specification, pageable);
        logger.debug("Found {} sale reports", saleReportsPage.getTotalElements());

        // Map the retrieved sale reports to ListReportsReq DTOs.
        List<ListReportsReq> reports =saleReportsPage.getContent().stream()
                .map(this::mapToListReportReq)
                .toList();
        logger.debug("Mapped {} sale reports to ListReportsReq", reports.size());

        logger.trace("Reports listed");

        return reports;
        }

    @Override
    public byte[] generatePdfReceipt(Long reportId) throws IOException {
        logger.trace("Generating pdf receipt...");

        SaleReport saleReport = saleReportRepository.findById(reportId)
                .orElseThrow(() -> {
                    logger.error("Report not found for id {}", reportId);
                    return new ReportNotFoundException("Report not found");
                });
        logger.debug("Found SaleReport: {}", saleReport);

        // Generate the PDF receipt bytes using the provided SaleReport.
        byte[] pdfBytes = receiptGenerator.generate(saleReport);

        logger.trace("Receipt generated");

        return pdfBytes;
    }

    @Override
    public void saveCampaign(CampaignDTO campaignDTO) {
        logger.trace("Saving campaign...");

        Optional<Campaign> campaign = campaignRepository.findByName(campaignDTO.getName());
        logger.debug("Retrieved Campaign: {}", campaign);

        if (campaign.isEmpty()){
            // If the campaign doesn't exist, create a new one and save it.
            Campaign newCampaign = new Campaign();
            newCampaign.setName(campaignDTO.getName());
            newCampaign.setDiscountAmount(campaignDTO.getDiscountAmount());
            newCampaign.setDiscountType(campaignDTO.getDiscountType());
            newCampaign.setNeededQuantity(campaignDTO.getNeededQuantity());

            campaignRepository.save(newCampaign);
        }
        else{
            // If the campaign exists, update its properties and save it.
            campaign.get().setNeededQuantity(campaignDTO.getNeededQuantity());
            campaign.get().setDiscountAmount(campaignDTO.getDiscountAmount());
            campaign.get().setDiscountType(campaignDTO.getDiscountType());

            campaignRepository.save(campaign.get());
        }

        logger.trace("Campaign saved");
    }

    /**
     * Applies the specified sorting criteria to the given pageable object.
     *
     * @param pageNo The number of the page to be sorted.
     * @param pageSize The size of the page to be sorted.
     * @param sortBy The field by which the data should be sorted.
     * @param sortOrder The order in which the data should be sorted (ASC or DESC).
     * @return A Pageable object with the specified sorting criteria applied.
     */
    private Pageable applySort(Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
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
     * This method retrieves a product based on its details and the associated sale report.
     *
     * @param productDTO The ProductDTO containing the details of the product to be retrieved.
     * @param saleReport The SaleReport object associated with the product.
     * @return A Product object containing the mapped data.
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
}