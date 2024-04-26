package bit.reportingservice.service;

import bit.reportingservice.dto.ListReportsReq;
import bit.reportingservice.dto.kafka.*;
import bit.reportingservice.entity.Campaign;
import bit.reportingservice.entity.DiscountType;
import bit.reportingservice.entity.Product;
import bit.reportingservice.entity.SaleReport;
import bit.reportingservice.exceptions.productnotfound.ProductNotFoundException;
import bit.reportingservice.exceptions.reportnotfound.ReportNotFoundException;
import bit.reportingservice.repository.CampaignRepository;
import bit.reportingservice.repository.ProductRepository;
import bit.reportingservice.repository.SaleReportRepository;
import bit.reportingservice.service.serviceimpl.ReportingServiceImpl;
import bit.reportingservice.utils.ReceiptGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportingServiceTest {
    @Mock
    private SaleReportRepository saleReportRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReceiptGenerator receiptGenerator;

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private ReportingServiceImpl reportingService;

    private CancelledSaleReportDTO cancelledSaleReportDTO;
    private SaleReport saleReport;
    private CampaignDTO campaignDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        List<Product> products = getProducts();

        cancelledSaleReportDTO = new CancelledSaleReportDTO();
        saleReport = new SaleReport();
        saleReport.setId(1L);
        saleReport.setProducts(products);

        campaignDTO = new CampaignDTO();
        campaignDTO.setName("Test Campaign");
        campaignDTO.setDiscountAmount(10D);
        campaignDTO.setDiscountType(DiscountType.PERCENTAGE);
        campaignDTO.setNeededQuantity(5);
    }

    @Test
    void testSaveSaleReport() {
        ProductDTO productDTO1 = new ProductDTO();
        ProductDTO productDTO2 = new ProductDTO();

        productDTO1.setId(1L);
        productDTO1.setName("Product 1");
        productDTO1.setPrice(10D);
        productDTO1.setQuantity(10);
        productDTO1.setReturned(false);
        productDTO1.setReturnedQuantity(0);

        productDTO2.setId(2L);
        productDTO2.setName("Product 2");
        productDTO2.setPrice(20D);
        productDTO2.setQuantity(20);
        productDTO2.setReturned(false);
        productDTO2.setReturnedQuantity(0);

        List<ProductDTO> products = new ArrayList<>();
        products.add(productDTO1);
        products.add(productDTO2);

        SaleReportDTO saleReportDTO = new SaleReportDTO();
        saleReportDTO.setProducts(products);

        reportingService.saveSaleReport(saleReportDTO);

        verify(saleReportRepository, times(1)).save(any(SaleReport.class));
    }

    @Test
    void testSaveCancelledStateOfSaleReport() {
        cancelledSaleReportDTO.setId(1L);
        cancelledSaleReportDTO.setCanceledDate(LocalDateTime.now());
        cancelledSaleReportDTO.setReturnedMoney(50.0);

        when(saleReportRepository.findById(1L)).thenReturn(Optional.of(saleReport));

        reportingService.saveCancelledStateOfSaleReport(cancelledSaleReportDTO);

        assertTrue(saleReport.isCancelled());
        assertEquals(cancelledSaleReportDTO.getCanceledDate(), saleReport.getCancelledDate());
        assertEquals(0D, saleReport.getTotalPrice());
        for (Product product : saleReport.getProducts()) {
            assertTrue(product.isReturned());
            assertEquals(product.getQuantity(), product.getReturnedQuantity());
            assertEquals(0, product.getQuantity());
        }
        verify(saleReportRepository, times(1)).save(saleReport);
    }

    @Test
    void testSaveCancelledStateOfSaleReport_ThrowsReportNotFoundException() {
        assertThrows(ReportNotFoundException.class, () -> reportingService.saveCancelledStateOfSaleReport(cancelledSaleReportDTO));
    }

    @Test
    void testUpdateProductAndSaleReport() {
        ReturnedProductInfoDTO returnedProductInfoDTO = new ReturnedProductInfoDTO();
        returnedProductInfoDTO.setId(1L);
        returnedProductInfoDTO.setReturned(true);
        returnedProductInfoDTO.setQuantity(0);
        returnedProductInfoDTO.setReturnedQuantity(2);
        returnedProductInfoDTO.setReturnedMoney(30.0);

        Product product = new Product();
        product.setId(1L);
        product.setReturned(false);
        product.setQuantity(2);
        product.setReturnedQuantity(0);

        SaleReport saleReport = new SaleReport();
        saleReport.setId(1L);
        saleReport.setTotalPrice(50.0);
        saleReport.setReturnedMoney(20.0);
        product.setSaleReport(saleReport);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        reportingService.updateProductAndSaleReport(returnedProductInfoDTO);

        assertTrue(product.isReturned());
        assertEquals(0, product.getQuantity());
        assertEquals(2, product.getReturnedQuantity());
        assertEquals(30.0, saleReport.getReturnedMoney());
        assertEquals(20.0, saleReport.getTotalPrice());
        verify(productRepository, times(1)).save(product);
        verify(saleReportRepository, times(1)).save(saleReport);
    }

    @Test
    void testUpdateProductAndSaleReport_ProductNotFound() {
        ReturnedProductInfoDTO returnedProductInfoDTO = new ReturnedProductInfoDTO();
        returnedProductInfoDTO.setId(999L);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> reportingService.updateProductAndSaleReport(returnedProductInfoDTO));

        verify(productRepository, never()).save(any());
        verify(saleReportRepository, never()).save(any());
    }

    @Test
    void testGeneratePdfReceipt() throws IOException {
        Long reportId = 1L;
        byte[] pdfBytes = "Generated PDF".getBytes();

        when(saleReportRepository.findById(reportId)).thenReturn(Optional.of(saleReport));

        when(receiptGenerator.generate(any(SaleReport.class))).thenReturn(pdfBytes);

        byte[] result = reportingService.generatePdfReceipt(reportId);

        verify(saleReportRepository, times(1)).findById(reportId);

        verify(receiptGenerator, times(1)).generate(saleReport);

        assertNotNull(result);
    }

    @Test
    void testSaveCampaign() {
        when(campaignRepository.findByName(campaignDTO.getName())).thenReturn(Optional.empty());

        reportingService.saveCampaign(campaignDTO);

        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    void testSaveCampaign_ThereIsACampaignWithSameName() {
        when(campaignRepository.findByName(campaignDTO.getName())).thenReturn(Optional.of(new Campaign()));

        reportingService.saveCampaign(campaignDTO);

        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    void listReports_shouldReturnListOfReports() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "completedDate"));
        List<SaleReport> pageContent = Collections.singletonList(saleReport);
        PageImpl<SaleReport> page = new PageImpl<>(pageContent, pageable, pageContent.size());

        when(saleReportRepository.findAll(ArgumentMatchers.<Specification<SaleReport>>any(), eq(pageable))).thenReturn(page);

        List<ListReportsReq> reports = reportingService.listReports(0, 10, null, "cash", "completedDate", "asc");

        assertEquals(pageContent.size(), reports.size());
    }

    private static List<Product> getProducts() {
        Product product1 = new Product();
        Product product2 = new Product();

        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10D);
        product1.setQuantity(0);
        product1.setReturned(true);
        product1.setReturnedQuantity(0);

        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(10D);
        product2.setQuantity(0);
        product2.setReturned(true);
        product2.setReturnedQuantity(0);

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        return products;
    }
}