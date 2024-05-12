package bit.salesservice.service;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.PaymentMethod;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.service.serviceimpl.CheckoutServiceImpl;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import bit.salesservice.validators.CheckoutValidator;
import lombok.Getter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @InjectMocks
    private CheckoutServiceImpl checkoutService;
    private static MockWebServer mockWebServer;
    private CompleteCheckoutReq completeCheckoutReq;
    private Checkout checkout;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        WebClient mockedWebClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        when(webClientConfig.webClient()).thenReturn(mockedWebClient);

        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()));

        completeCheckoutReq = new CompleteCheckoutReq();
        completeCheckoutReq.setPaymentMethod("CASH");
        completeCheckoutReq.setMoneyTaken(120D);

        List<Product> products = new ArrayList<>();
        products.add(new Product());

        checkout = new Checkout();
        checkout.setPaymentMethod(PaymentMethod.CASH);
        checkout.setTotalPrice(125D);
        checkout.setMoneyTaken(120D);
        checkout.setChange(5D);
        checkout.setProducts(products);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.close();
    }

//    @Test
//    void testCancelCheckout_Success() {
//        Checkout checkout = new Checkout();
//        checkout.setId(1L);
//        checkout.setCancelled(false);
//        checkout.setTotalPrice(100.0);
//        checkout.setProducts(Collections.singletonList(new Product()));
//
//        when(checkoutRepository.findById(1L)).thenReturn(Optional.of(checkout));
//
//        checkoutService.cancelCheckout(1L);
//
//        verify(checkoutRepository, times(1)).findById(1L);
//        verify(checkoutRepository, times(1)).save(checkout);
//    }

    @Test
    void testCancelCheckout_Fail_ThrowsCheckoutNotFoundException() {

        assertThrows(CheckoutNotFoundException.class, () -> checkoutService.cancelCheckout(1L));
    }

//    @Test
//    void testCompleteCheckout_Success_WithCashMethod() {
//        when(checkoutRepository.findFirstByOrderByIdDesc()).thenReturn(checkout);
//
//        checkoutService.completeCheckout(completeCheckoutReq, checkoutId);
//
//        verify(checkoutRepository, times(2)).save(any(Checkout.class));
//    }
//
//    @Test
//    void testCompleteCheckout_Success_WithCreditCardMethod() {
//        completeCheckoutReq.setPaymentMethod("CREDIT_CARD");
//
//        when(checkoutRepository.findFirstByOrderByIdDesc()).thenReturn(checkout);
//
//        checkoutService.completeCheckout(completeCheckoutReq, checkoutId);
//
//        verify(checkoutRepository, times(2)).save(any(Checkout.class));
//        assertEquals(checkout.getTotalPrice(), checkout.getMoneyTaken());
//        assertEquals(0D, checkout.getChange());
//    }

}
