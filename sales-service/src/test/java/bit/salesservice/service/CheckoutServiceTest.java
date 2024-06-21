package bit.salesservice.service;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.entity.*;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.repository.ShoppingBagRepository;
import bit.salesservice.service.serviceimpl.CheckoutServiceImpl;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import bit.salesservice.validators.BagValidator;
import bit.salesservice.validators.CheckoutValidator;
import lombok.Getter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceTest {
    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private WebClientConfig webClientConfig;
    @Getter
    @Mock
    private CheckoutValidator checkoutValidator;
    @Getter
    @Mock
    private SaleReportProducer saleReportProducer;
    @Getter
    @Mock
    private ProductInfoHttpRequest request;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ShoppingBagRepository shoppingBagRepository;
    @Mock
    @Getter
    private BagValidator bagValidator;
    @InjectMocks
    private CheckoutServiceImpl checkoutService;
    private static MockWebServer mockWebServer;
    private CompleteCheckoutReq completeCheckoutReq;
    private Checkout checkout;
    private ProductInfo productInfo;
    private AddAndListProductReq addAndListProductReq;
    private Product product;
    private Campaign campaign;
    private Long checkoutId;
    private RemoveOrReturnProductFromBagReq req;
    private List<RemoveOrReturnProductFromBagReq> reqs;
    private List<AddAndListProductReq> addAndListProductReqs;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        WebClient mockedWebClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        when(webClientConfig.webClient()).thenReturn(mockedWebClient);

        productInfo = new ProductInfo();
        when(request.getProductInfo(anyString(), anyString())).thenReturn(productInfo);

        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()));

        productInfo.setExists(true);
        productInfo.setStockAmount(10);

        addAndListProductReq = new AddAndListProductReq();
        addAndListProductReq.setCode("productCode");
        addAndListProductReq.setQuantity(10);

        completeCheckoutReq = new CompleteCheckoutReq();
        completeCheckoutReq.setMoneyTakenFromCard(5D);
        completeCheckoutReq.setMoneyTakenFromCash(120D);

        List<Product> products = new ArrayList<>();
        products.add(new Product());

        checkout = new Checkout();
        checkout.setId(1L);
        checkout.setPaymentMethod(PaymentMethod.CASH);
        checkout.setTotalPrice(125D);
        checkout.setMoneyTaken(120D);
        checkout.setChange(5D);
        checkout.setProducts(products);

        product = new Product();
        product.setCode("productCode");
        product.setQuantity(3);
        product.setCheckout(checkout);

        campaign = new Campaign();
        campaign.setDiscountType(DiscountType.PERCENTAGE);

        checkoutId = 1L;

        req = new RemoveOrReturnProductFromBagReq();
        req.setCode("productCode");
        req.setQuantity(2);

        reqs = new ArrayList<>();
        reqs.add(req);

        addAndListProductReqs = new ArrayList<>();
        addAndListProductReqs.add(addAndListProductReq);

        when(checkoutRepository.findById(checkoutId)).thenReturn(Optional.of(checkout));
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    void testCancelCheckout_Success() {
        Checkout checkout = new Checkout();
        checkout.setId(1L);
        checkout.setCancelled(false);
        checkout.setTotalPrice(100.0);
        checkout.setProducts(Collections.singletonList(new Product()));

        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));

        checkoutService.cancelCheckout(1L);

        verify(checkoutRepository, times(1)).findById(1L);
        verify(checkoutRepository, times(1)).save(checkout);
    }

    @Test
    void testCancelCheckout_Fail_ThrowsCheckoutNotFoundException() {
        checkoutId = 2L;
        assertThrows(CheckoutNotFoundException.class, () -> checkoutService.cancelCheckout(checkoutId));
    }

    @Test
    void testCompleteCheckout_Success_WithCashMethod() {
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));

        checkoutService.completeCheckout(completeCheckoutReq, 1L);

        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }

    @Test
    void testCompleteCheckout_Success_WithCreditCardMethod() {
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));

        checkoutService.completeCheckout(completeCheckoutReq, 1L);

        verify(checkoutRepository, times(1)).save(any(Checkout.class));
        assertEquals(checkout.getTotalPrice(), checkout.getMoneyTaken());
        assertEquals(0D, checkout.getChange());
    }

    @Test
    void addProductToBag_ProductNotFound_ThrowsProductNotFoundException() {
        productInfo.setExists(false);
        productInfo.setStockAmount(0);

        Assertions.assertThrows(ProductNotFoundException.class, () -> checkoutService.addProductsToBag(addAndListProductReqs, checkoutId));
    }

    @Test
    void addProductToBag_NotInStocks_ThrowsNotInStocksException() {
        productInfo.setStockAmount(addAndListProductReq.getQuantity() - 1);

        Assertions.assertThrows(NotInStocksException.class, () -> checkoutService.addProductsToBag(addAndListProductReqs, checkoutId));
    }

    @Test
    void addProductToBag_WithRemovedProduct() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(product);
        product.setRemoved(true);

        checkoutService.addProductsToBag(addAndListProductReqs, checkoutId);

        verify(shoppingBagRepository, times(1)).save(product);
    }

    @Test
    void addProductToBag_WithUnRemovedProduct() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(product);
        productInfo.setStockAmount(20);
        checkoutService.addProductsToBag(addAndListProductReqs, checkoutId);

        verify(shoppingBagRepository, times(1)).save(product);
    }

    @Test
    void addProductToBag_WithUnRemovedProduct_ThrowsNotInStocksException() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(product);
        productInfo.setStockAmount(product.getQuantity() + addAndListProductReq.getQuantity() - 1);

        Assertions.assertThrows(NotInStocksException.class, () -> checkoutService.addProductsToBag(addAndListProductReqs, checkoutId));
    }

    @Test
    void addProductToBag_WithNewProduct() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);

        checkoutService.addProductsToBag(addAndListProductReqs, checkoutId);

        verify(shoppingBagRepository, times(1)).save(any(Product.class));
    }

    @Test
    void addProductToBag_WithNewProduct_WithCampaign() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);

        campaign.setNeededQuantity(2);

        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);

        checkoutService.addProductsToBag(addAndListProductReqs, checkoutId);

        verify(shoppingBagRepository, times(1)).save(any(Product.class));
    }

    @Test
    void addProductToBag_WithNewProduct_WithCampaign_WithNeededQuantity1() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);

        campaign.setNeededQuantity(1);

        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);

        checkoutService.addProductsToBag(addAndListProductReqs, checkoutId);

        verify(shoppingBagRepository, times(1)).save(any(Product.class));
    }

    @Test
    void addProductToBag_WithNewProduct_WithCampaign_WithFixedAmountPercentageType() {
        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);

        campaign.setNeededQuantity(1);
        campaign.setDiscountType(DiscountType.FIXED_AMOUNT);

        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);

        checkoutService.addProductsToBag(addAndListProductReqs, checkoutId);

        verify(shoppingBagRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductsInBagForCurrentCheckout_NotCompletedCheckout() {
        checkout.setCompleted(false);

        List<AddAndListProductReq> expectedProducts = Arrays.asList(new AddAndListProductReq(), new AddAndListProductReq());
        when(shoppingBagRepository.findProductReqByCheckoutAndRemoved(checkout)).thenReturn(expectedProducts);

        List<AddAndListProductReq> actualProducts = checkoutService.getProductsInBag(checkoutId);

        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void getProductsInBagForCurrentCheckout_CompletedCheckout() {
        checkout.setCompleted(true);

        List<AddAndListProductReq> actualProducts = checkoutService.getProductsInBag(checkoutId);

        assertEquals(Collections.emptyList(), actualProducts);
    }

    @Test
    void removeAll_RemovesAllProducts() {
        checkout.setId(1L);
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));

        when(shoppingBagRepository.findByCheckoutId(checkout.getId())).thenReturn(List.of(product));

        checkoutService.removeAll(checkoutId);

        assertTrue(product.isRemoved());
        assertEquals(0, product.getQuantity());

        verify(shoppingBagRepository).saveAll(List.of(product));

        assertEquals(0D, checkout.getTotalPrice());
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void removeAll_NoProductsInShoppingBag() {
        checkout.setId(1L);
        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));

        when(shoppingBagRepository.findByCheckoutId(checkout.getId())).thenReturn(Collections.emptyList());

        checkoutService.removeAll(checkoutId);

        assertEquals(0D, checkout.getTotalPrice());
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void removeProductFromBag_QuantityAndProductQuantityEquals() {
        when(shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout)).thenReturn(product);

        req.setQuantity(3);

        checkoutService.removeProductsFromBag(reqs, 1L);

        assertTrue(product.isRemoved());

        verify(shoppingBagRepository).save(product);
    }

    @Test
    void removeProductFromBag_ProductExistsAndQuantityValid() {
        when(shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout)).thenReturn(product);

        checkoutService.removeProductsFromBag(reqs, 1L);

        assertEquals(1, product.getQuantity());
        assertFalse(product.isRemoved());

        verify(shoppingBagRepository).save(product);
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForRemoveProductFromBag")
    void removeProductFromBag_ProductExistsAndQuantityValid_ProductHasCampaign_NeededQuantityIsOne(
            Campaign campaign, int neededQuantity, int quantityToRemove
    ) {
        campaign.setNeededQuantity(neededQuantity);
        campaign.setDiscountType(DiscountType.PERCENTAGE);

        when(shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout)).thenReturn(product);
        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);

        req.setQuantity(quantityToRemove);

        checkoutService.removeProductsFromBag(reqs, 1L);

        verify(shoppingBagRepository).save(product);
    }

    static Stream<Arguments> provideTestDataForRemoveProductFromBag() {
        return Stream.of(
                Arguments.of(new Campaign(), 1, 2),
                Arguments.of(new Campaign(), 2, 2),
                Arguments.of(new Campaign(), 4, 2)
        );
    }

    @Test
    void removeProductFromBag_ProductExistsAndQuantityValid_ProductHasCampaign_NeededQuantityIsGreaterThenOne_FixedAmountType() {
        campaign.setNeededQuantity(1);
        campaign.setDiscountType(DiscountType.FIXED_AMOUNT);

        when(shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout)).thenReturn(product);
        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);

        checkoutService.removeProductsFromBag(reqs, 1L);

        verify(shoppingBagRepository).save(product);
    }

    @Test
    void removeProductFromBag_ProductNotFound_ThrowsProductNotFoundException() {
        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> checkoutService.removeProductsFromBag(reqs, 1L));
    }

    @Test
    void returnProductFromBag_ProductNotFound() {
        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> checkoutService.returnProductFromBag(req, 1L));
    }

    @Test
    void returnProductFromBag_UncompletedCheckout_ThrowsException() {
        product.setId(1L);
        product.setCheckout(checkout);

        when(shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout)).thenReturn(product);

        product.getCheckout().setCompleted(false);

        Assertions.assertThrows(UncompletedCheckoutException.class, () -> checkoutService.returnProductFromBag(req, 1L));
    }

    @Test
    void returnProductFromBag_CompletedCheckout_ProductRemovedOrReturned() {
        product.setId(1L);
        product.setCheckout(checkout);
        product.setReturned(true);

        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
        product.getCheckout().setCompleted(true);

        Assertions.assertThrows(ProductNotFoundException.class, () -> checkoutService.returnProductFromBag(req, 1L));
    }

    @ParameterizedTest
    @MethodSource("provideTestDataForReturnProductFromBag")
    void returnProductFromBag_CompletedCheckout_Success(
            Integer quantityToReturn
    ) {
        product.setId(1L);
        product.setCheckout(checkout);

        when(shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout)).thenReturn(product);

        product.getCheckout().setCompleted(true);

        req.setQuantity(quantityToReturn);

        checkoutService.returnProductFromBag(req, 1L);

        verify(shoppingBagRepository).save(product);
    }

    static Stream<Arguments> provideTestDataForReturnProductFromBag() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(3)
        );
    }
}