package bit.salesservice.controller;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

class CheckoutControllerTest {
    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void completeCheckout_Success() {
        CompleteCheckoutReq request = new CompleteCheckoutReq();

        doNothing().when(checkoutService).completeCheckout(request);

        ResponseEntity<String> response = checkoutController.completeCheckout(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Checkout completed successfully", response.getBody());
    }

    @Test
    void cancelCheckout_Success() {
        Long checkoutId = 1L;

        doNothing().when(checkoutService).cancelCheckout(checkoutId);

        ResponseEntity<String> response = checkoutController.cancelCheckout(checkoutId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Checkout canceled successfully", response.getBody());
    }
}
