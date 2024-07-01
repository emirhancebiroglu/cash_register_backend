package bit.reportingservice.utils;

import bit.reportingservice.entity.*;
import bit.reportingservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ReceiptGeneratorTest {
    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private ReceiptGenerator receiptGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateReceipt() throws IOException {
        LocalDateTime completedDate = LocalDateTime.now();
        SaleReport saleReport = new SaleReport();
        saleReport.setCompletedDate(completedDate);
        saleReport.setId(1L);
        saleReport.setMoneyTaken(100.0);
        saleReport.setReturnedMoney(0.0);
        saleReport.setChange(10.0);
        saleReport.setTotalPrice(90.0);
        saleReport.setCancelled(false);
        saleReport.setPaymentMethod(PaymentMethod.PARTIAL);

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setCode("P001");
        product.setName("Product 1");
        product.setPrice(20.0);
        product.setQuantity(2);
        product.setReturnedQuantity(0);
        product.setAppliedCampaign("Campaign1");

        products.add(product);
        saleReport.setProducts(products);

        Campaign campaign = new Campaign();
        campaign.setName("Campaign1");
        campaign.setDiscountAmount(10.0);
        campaign.setNeededQuantity(2);
        campaign.setDiscountType(DiscountType.PERCENTAGE);


        when(campaignRepository.findByName("Campaign1")).thenReturn(Optional.of(campaign));

        byte[] receipt = receiptGenerator.generate(saleReport);

        assertNotNull(receipt);
        assertTrue(receipt.length > 0);
    }
}
