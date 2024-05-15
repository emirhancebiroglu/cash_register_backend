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
import static org.mockito.Mockito.verify;

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

        doNothing().when(checkoutService).completeCheckout(request, 1L);

        ResponseEntity<String> response = checkoutController.completeCheckout(request, 1L);

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

    @Test
    void openSale_Success() {
        // Arrange
        doNothing().when(checkoutService).openSale();

        // Act
        ResponseEntity<String> response = checkoutController.openSale();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sale opened successfully", response.getBody());

        // Verify that the openSale method of checkoutService was called
        verify(checkoutService).openSale();
    }
}
