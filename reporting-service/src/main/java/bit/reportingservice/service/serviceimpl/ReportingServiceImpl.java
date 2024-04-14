package bit.reportingservice.service.serviceimpl;

import bit.reportingservice.dto.ListProductReq;
import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.CancelledSaleReportDTO;
import bit.reportingservice.dto.kafka.ProductDTO;
import bit.reportingservice.dto.kafka.ReturnedProductInfoDTO;
import bit.reportingservice.dto.kafka.SaleReportDTO;
import bit.reportingservice.entity.*;
import bit.reportingservice.repository.ProductRepository;
import bit.reportingservice.repository.SaleReportRepository;
import bit.reportingservice.service.ReportingService;
import bit.reportingservice.utils.ReceiptGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {
    private final SaleReportRepository saleReportRepository;
    private final ProductRepository productRepository;
    private final ReceiptGenerator receiptGenerator;
    @Override
    public void saveSaleReport(SaleReportDTO saleReportDTO) {
        SaleReport saleReport = mapToSaleReport(saleReportDTO);

        saleReportRepository.save(saleReport);
    }

    @Override
    public void saveCancelledStateOfSaleReport(CancelledSaleReportDTO cancelledSaleReportDTO) {
        SaleReport saleReport = saleReportRepository.findById(cancelledSaleReportDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("notfound"));

        saleReport.setCancelled(true);
        saleReport.setCancelledDate(cancelledSaleReportDTO.getCanceledDate());
        saleReport.setReturnedMoney(cancelledSaleReportDTO.getReturnedMoney());

        for (Product product : saleReport.getProducts()) {
            product.setReturned(true);
            product.setReturnedQuantity(product.getQuantity());
            product.setQuantity(0);
        }

        saleReportRepository.save(saleReport);
    }

    @Override
    public void updateProductAndSaleReport(ReturnedProductInfoDTO returnedProductInfoDTO) {
        Product product = productRepository.findById(returnedProductInfoDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("notfound"));

        product.setReturned(returnedProductInfoDTO.getReturned());
        product.setQuantity(returnedProductInfoDTO.getQuantity());
        product.setReturnedQuantity(returnedProductInfoDTO.getReturnedQuantity());
        product.getSaleReport().setReturnedMoney(returnedProductInfoDTO.getReturnedMoney());
        product.getSaleReport().setTotalPrice(product.getSaleReport().getTotalPrice() - returnedProductInfoDTO.getReturnedMoney());

        productRepository.save(product);
        saleReportRepository.save(product.getSaleReport());
    }

    @Override
    public List<ListReportsReq> listReports(int page, int size, SortBy sortBy, FilterBy filterBy, PaymentMethod paymentMethod) {
        if (filterBy != null){
            if (filterBy == FilterBy.PAYMENT_METHOD && (paymentMethod == null || !EnumSet.allOf(PaymentMethod.class).contains(paymentMethod))){
                throw new IllegalArgumentException("invalid payment method provided");
            }

            if (!EnumSet.allOf(FilterBy.class).contains(filterBy)){
                throw new IllegalArgumentException("Invalid filter method");
            }
        }

        if (!EnumSet.allOf(SortBy.class).contains(sortBy)){
                throw new IllegalArgumentException("Invalid sort method");
        }

        Pageable pageable = PageRequest.of(page, size, getSort(sortBy));

        Page<SaleReport> saleReportsPage = filterReports(filterBy, paymentMethod, pageable);

        return saleReportsPage.map(this::mapToListReportReq).getContent();
    }

    @Override
    public byte[] generatePdfReceipt(Long reportId) throws IOException {
        SaleReport saleReport = saleReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalStateException("No such report"));

        return receiptGenerator.generate(saleReport);
    }

    private Page<SaleReport> filterReports(FilterBy filterBy, PaymentMethod paymentMethod, Pageable pageable) {
        if (filterBy == null) {
            return saleReportRepository.findAll(pageable);
        }

        return switch (filterBy) {
            case CANCELLED_ONLY -> saleReportRepository.findByCancelled(true, pageable);
            case PAYMENT_METHOD -> saleReportRepository.findByPaymentMethod(paymentMethod, pageable);
        };
    }

    private Sort getSort(SortBy sortBy) {
        return switch (sortBy) {
            case COMPLETED_DATE_DESC -> Sort.by(Sort.Direction.DESC, "completedDate");
            case TOTAL_PRICE_DESC -> Sort.by(Sort.Direction.DESC, "totalPrice");
        };
    }

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
