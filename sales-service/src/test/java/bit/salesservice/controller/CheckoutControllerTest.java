package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CheckoutControllerTest {
    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;
    private RemoveOrReturnProductFromBagReq req;
    private List<RemoveOrReturnProductFromBagReq> reqs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        req = new RemoveOrReturnProductFromBagReq();
        req.setCode("test");
        req.setQuantity(2);

        reqs = new ArrayList<>();
        reqs.add(req);
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
    void createCheckout_Success() {
        List<AddAndListProductReq> reqs = new ArrayList<>();
        reqs.add(new AddAndListProductReq());

        // Arrange
        doNothing().when(checkoutService).createCheckout(reqs);

        // Act
        ResponseEntity<String> response = checkoutController.createCheckout(reqs);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Checkout created successfully", response.getBody());

        // Verify that the openSale method of checkoutService was called
        verify(checkoutService).createCheckout(reqs);
    }

    @Test
    void addProductToShoppingBag_Success() {
        List<AddAndListProductReq> reqs = new ArrayList<>();

        doNothing().when(checkoutService).addProductsToBag(reqs, 1L);

        ResponseEntity<String> response = checkoutController.addProductsToShoppingBag(reqs, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Products added successfully", response.getBody());
    }

    @Test
    void removeProductFromShoppingBag_Success() {
        doNothing().when(checkoutService).removeProductsFromBag(reqs, 1L);

        ResponseEntity<String> response = checkoutController.removeProductsFromShoppingBag(reqs, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Products removed successfully", response.getBody());
    }

    @Test
    void returnProductFromShoppingBag_Success() {
        doNothing().when(checkoutService).returnProductFromBag(req, 1L);

        ResponseEntity<String> response = checkoutController.returnProductFromShoppingBag(req, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product returned successfully", response.getBody());
    }

    @Test
    void removeAll_Success() {
        doNothing().when(checkoutService).removeAll(1L);

        ResponseEntity<String> response = checkoutController.removeAll(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bag is cleaned successfully", response.getBody());
    }

    @Test
    void getProductsInShoppingBagForCurrentCheckout_Success() {
        List<AddAndListProductReq> productsInBag = List.of(new AddAndListProductReq(), new AddAndListProductReq());
        when(checkoutService.getProductsInBag(1L)).thenReturn(productsInBag);

        List<AddAndListProductReq> response = checkoutController.getProductsInBag(1L);

        assertEquals(productsInBag, response);
    }
}