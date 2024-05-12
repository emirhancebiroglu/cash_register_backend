package bit.salesservice.service;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.repository.ShoppingBagRepository;
import bit.salesservice.service.serviceimpl.ShoppingBagServiceImpl;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import lombok.Getter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ShoppingBagServiceTest {
    @Mock
    private ShoppingBagRepository shoppingBagRepository;
    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private WebClientConfig webClientConfig;
    @Getter
    @Mock
    private SaleReportProducer saleReportProducer;
    @Mock
    private ProductInfoHttpRequest request;
    @InjectMocks
    private ShoppingBagServiceImpl shoppingBagService;
    private static MockWebServer mockWebServer;
    private ProductInfo productInfo;
    private AddAndListProductReq addAndListProductReq;
    private Product product;
    private Checkout checkout;
    private Campaign campaign;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        WebClient mockedWebClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        when(webClientConfig.webClient()).thenReturn(mockedWebClient);

        productInfo = new ProductInfo();

        when(request.getProductInfo(anyString(), anyString())).thenReturn(productInfo);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value()));

        productInfo.setExists(true);
        productInfo.setStockAmount(10);

        addAndListProductReq = new AddAndListProductReq();
        addAndListProductReq.setCode("productCode");
        addAndListProductReq.setQuantity(10);


        checkout = new Checkout();
        when(checkoutRepository.findFirstByOrderByIdDesc()).thenReturn(checkout);
        checkout.setTotalPrice(0D);

        product = new Product();
        product.setCode("productCode");
        product.setQuantity(3);
        product.setCheckout(checkout);

        campaign = new Campaign();
        campaign.setDiscountType(DiscountType.PERCENTAGE);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.close();
    }

//    @Test
//    void addProductToBag_InvalidQuantity_ThrowsInvalidQuantityException() {
//        addAndListProductReq.setQuantity(-1);
//
//        assertThrows(InvalidQuantityException.class, () -> shoppingBagService.addProductToBag(addAndListProductReq, checkoutId));
//    }
//
//    @Test
//    void addProductToBag_ProductNotFound_ThrowsProductNotFoundException() {
//        productInfo.setExists(false);
//        productInfo.setStockAmount(0);
//
//        assertThrows(ProductNotFoundException.class, () -> shoppingBagService.addProductToBag(addAndListProductReq, checkoutId));
//    }

//    @Test
//    void addProductToBag_NotInStocks_ThrowsNotInStocksException() {
//        productInfo.setStockAmount(addAndListProductReq.getQuantity() - 1);
//
//        assertThrows(NotInStocksException.class, () -> shoppingBagService.addProductToBag(addAndListProductReq, checkoutId));
//    }
//
//    @Test
//    void addProductToBag_WithRemovedProduct() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(product);
//        product.setRemoved(true);
//
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(shoppingBagRepository, times(1)).save(product);
//    }
//
//    @Test
//    void addProductToBag_WithUnRemovedProduct() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(product);
//        productInfo.setStockAmount(20);
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(shoppingBagRepository, times(1)).save(product);
//    }
//
//    @Test
//    void addProductToBag_WithUnRemovedProduct_ThrowsNotInStocksException() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(product);
//        productInfo.setStockAmount(product.getQuantity() + addAndListProductReq.getQuantity() - 1);
//
//        assertThrows(NotInStocksException.class, () -> shoppingBagService.addProductToBag(addAndListProductReq, checkoutId));
//    }
//
//    @Test
//    void addProductToBag_WithNewProduct() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);
//
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(shoppingBagRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void addProductToBag_WithNewProduct_WithCampaign() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);
//
//        campaign.setNeededQuantity(2);
//
//        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);
//
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(shoppingBagRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void addProductToBag_WithNewProduct_WithCampaign_WithNeededQuantity1() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);
//
//        campaign.setNeededQuantity(1);
//
//        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);
//
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(shoppingBagRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void addProductToBag_WithNewProduct_WithCampaign_WithFixedAmountPercentageType() {
//        when(shoppingBagRepository.findByCodeAndCheckout(addAndListProductReq.getCode(), checkout)).thenReturn(null);
//
//        campaign.setNeededQuantity(1);
//        campaign.setDiscountType(DiscountType.FIXED_AMOUNT);
//
//        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);
//
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(shoppingBagRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void addProductToBag_WithNullCheckout() {
//        when(checkoutRepository.findFirstByOrderByIdDesc()).thenReturn(null);
//
//        shoppingBagService.addProductToBag(addAndListProductReq, checkoutId);
//
//        verify(checkoutRepository, times(2)).save(any(Checkout.class));
//    }

    @Test
    void getProductsInBagForCurrentCheckout_NotCompletedCheckout() {
        checkout.setCompleted(false);

        List<AddAndListProductReq> expectedProducts = Arrays.asList(new AddAndListProductReq(), new AddAndListProductReq());
        when(shoppingBagRepository.findProductReqByCheckoutAndRemoved(checkout)).thenReturn(expectedProducts);

        List<AddAndListProductReq> actualProducts = shoppingBagService.getProductsInBagForCurrentCheckout();

        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void getProductsInBagForCurrentCheckout_CompletedCheckout() {
        checkout.setCompleted(true);

        List<AddAndListProductReq> actualProducts = shoppingBagService.getProductsInBagForCurrentCheckout();

        assertEquals(Collections.emptyList(), actualProducts);
    }

//    @Test
//    void removeAll_RemovesAllProducts() {
//        checkout.setId(1L);
//        when(checkoutRepository.findFirstByOrderByIdDesc()).thenReturn(checkout);
//
//        when(shoppingBagRepository.findByCheckoutId(checkout.getId())).thenReturn(List.of(product));
//
//        shoppingBagService.removeAll(checkoutId);
//
//        assertTrue(product.isRemoved());
//        assertEquals(0, product.getQuantity());
//
//        verify(shoppingBagRepository).saveAll(List.of(product));
//
//        assertEquals(0D, checkout.getTotalPrice());
//        verify(checkoutRepository).save(checkout);
//    }
//
//    @Test
//    void removeAll_NoProductsInShoppingBag() {
//        checkout.setId(1L);
//        when(checkoutRepository.findFirstByOrderByIdDesc()).thenReturn(checkout);
//
//        when(shoppingBagRepository.findByCheckoutId(checkout.getId())).thenReturn(Collections.emptyList());
//
//        shoppingBagService.removeAll(checkoutId);
//
//        assertEquals(0D, checkout.getTotalPrice());
//        verify(checkoutRepository).save(checkout);
//    }

//    @Test
//    void removeProductFromBag_QuantityAndProductQuantityEquals() {
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        shoppingBagService.removeProductFromBag(1L, 3);
//
//        assertTrue(product.isRemoved());
//
//        verify(shoppingBagRepository).save(product);
//    }
//
//    @Test
//    void removeProductFromBag_ProductExistsAndQuantityValid() {
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        shoppingBagService.removeProductFromBag(1L, 2);
//
//        assertEquals(1, product.getQuantity());
//        assertFalse(product.isRemoved());
//
//        verify(shoppingBagRepository).save(product);
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideTestDataForRemoveProductFromBag")
//    void removeProductFromBag_ProductExistsAndQuantityValid_ProductHasCampaign_NeededQuantityIsOne(
//            Campaign campaign, int neededQuantity, int quantityToRemove
//    ) {
//        campaign.setNeededQuantity(neededQuantity);
//        campaign.setDiscountType(DiscountType.PERCENTAGE);
//
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);
//
//        shoppingBagService.removeProductFromBag(1L, quantityToRemove);
//
//        verify(shoppingBagRepository).save(product);
//    }
//
//    static Stream<Arguments> provideTestDataForRemoveProductFromBag() {
//        return Stream.of(
//                Arguments.of(new Campaign(), 1, 2),
//                Arguments.of(new Campaign(), 2, 2),
//                Arguments.of(new Campaign(), 4, 2)
//        );
//    }
//
//    @Test
//    void removeProductFromBag_ProductExistsAndQuantityValid_ProductHasCampaign_NeededQuantityIsGreaterThenOne_FixedAmountType() {
//        campaign.setNeededQuantity(1);
//        campaign.setDiscountType(DiscountType.FIXED_AMOUNT);
//
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(campaignRepository.findFirstByCodesContaining(product.getCode())).thenReturn(campaign);
//
//        shoppingBagService.removeProductFromBag(1L, 2);
//
//        verify(shoppingBagRepository).save(product);
//    }
//
//    @Test
//    void removeProductFromBag_ProductNotFound_ThrowsProductNotFoundException() {
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () -> shoppingBagService.removeProductFromBag(1L, 2));
//    }
//
//    @Test
//    void removeProductFromBag_ProductExistsAndInvalidQuantity_Quantity0() {
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        assertThrows(InvalidQuantityException.class, () -> shoppingBagService.removeProductFromBag(1L, 0));
//    }
//
//    @Test
//    void removeProductFromBag_ProductExistsAndInvalidQuantity_QuantityBiggerThanProductQuantity() {
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        assertThrows(InvalidQuantityException.class, () -> shoppingBagService.removeProductFromBag(1L, 4));
//    }

//    @Test
//    void returnProductFromBag_ProductNotFound() {
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ProductNotFoundException.class, () -> shoppingBagService.returnProductFromBag(1L, 3));
//    }
//
//    @Test
//    void returnProductFromBag_UncompletedCheckout_ThrowsException() {
//        product.setId(1L);
//        product.setCheckout(checkout);
//
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//        product.getCheckout().setCompleted(false);
//
//        assertThrows(UncompletedCheckoutException.class, () -> shoppingBagService.returnProductFromBag(1L, 3));
//    }
//
//    @Test
//    void returnProductFromBag_CompletedCheckout_ProductRemovedOrReturned() {
//        product.setId(1L);
//        product.setCheckout(checkout);
//        product.setReturned(true);
//
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//        product.getCheckout().setCompleted(true);
//
//        assertThrows(ProductNotFoundException.class, () -> shoppingBagService.returnProductFromBag(1L, 3));
//    }
//
//    @Test
//    void returnProductFromBag_CompletedCheckout_QuantityToReturnIsBiggerThanProductQuantity() {
//        product.setId(1L);
//        product.setCheckout(checkout);
//
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//        product.getCheckout().setCompleted(true);
//
//        assertThrows(InvalidQuantityException.class, () -> shoppingBagService.returnProductFromBag(1L, 13));
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideTestDataForReturnProductFromBag")
//    void returnProductFromBag_CompletedCheckout_Success(
//            Integer quantityToReturn
//    ) {
//        product.setId(1L);
//        product.setCheckout(checkout);
//
//        when(shoppingBagRepository.findById(1L)).thenReturn(Optional.of(product));
//        product.getCheckout().setCompleted(true);
//
//        shoppingBagService.returnProductFromBag(1L, quantityToReturn);
//
//        verify(shoppingBagRepository).save(product);
//    }

    static Stream<Arguments> provideTestDataForReturnProductFromBag() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(3)
        );
    }
}
