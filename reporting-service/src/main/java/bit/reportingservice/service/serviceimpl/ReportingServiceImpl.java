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

    @Override
    public List<ListReportsReq> listReports(int page, int size, String cancelledStatus, String paymentStatus, String sortBy, String sortOrder) {
            logger.info("Listing reports...");

            Pageable pageable = applySort(page, size, sortBy, sortOrder);

            Page<SaleReport> saleReportsPage;

            Specification<SaleReport> specification = Specification.where((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (paymentStatus != null) {
                    if (paymentStatus.equalsIgnoreCase("cash")) {
                        predicates.add(criteriaBuilder.equal(root.get("paymentMethod"), PaymentMethod.CASH));
                    } else if (paymentStatus.equalsIgnoreCase("credit-card")) {
                        predicates.add(criteriaBuilder.equal(root.get("paymentMethod"), PaymentMethod.CREDIT_CARD));
                    }
                }
                if (cancelledStatus != null) {
                    predicates.add(criteriaBuilder.equal(root.get("cancelled"), cancelledStatus.equalsIgnoreCase("cancelled")));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });

            saleReportsPage = saleReportRepository.findAll(specification, pageable);

            List<ListReportsReq> reports =saleReportsPage.getContent().stream()
                            .map(this::mapToListReportReq)
                            .toList();

            logger.info("Reports listed");

            return reports;
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
