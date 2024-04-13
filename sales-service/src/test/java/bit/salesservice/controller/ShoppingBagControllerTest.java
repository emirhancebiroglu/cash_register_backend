package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.service.ShoppingBagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ShoppingBagControllerTest {
    @Mock
    private ShoppingBagService shoppingBagService;

    @InjectMocks
    private ShoppingBagController shoppingBagController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProductToShoppingBag_Success() {
        AddAndListProductReq request = new AddAndListProductReq();

        doNothing().when(shoppingBagService).addProductToBag(request);

        ResponseEntity<String> response = shoppingBagController.addProductToShoppingBag(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product added successfully", response.getBody());
    }

    @Test
    void removeProductFromShoppingBag_Success() {
        Long productId = 1L;
        Integer quantity = 2;

        doNothing().when(shoppingBagService).removeProductFromBag(productId, quantity);

        ResponseEntity<String> response = shoppingBagController.removeProductFromShoppingBag(productId, quantity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product removed successfully", response.getBody());
    }

    @Test
    void returnProductFromShoppingBag_Success() {
        Long productId = 1L;
        Integer quantityToReturn = 2;

        doNothing().when(shoppingBagService).returnProductFromBag(productId, quantityToReturn);

        ResponseEntity<String> response = shoppingBagController.returnProductFromShoppingBag(productId, quantityToReturn);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product returned successfully", response.getBody());
    }

    @Test
    void removeAll_Success() {
        doNothing().when(shoppingBagService).removeAll();

        ResponseEntity<String> response = shoppingBagController.removeAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bag is cleaned successfully", response.getBody());
    }

    @Test
    void getProductsInShoppingBagForCurrentCheckout_Success() {
        List<AddAndListProductReq> productsInBag = List.of(new AddAndListProductReq(), new AddAndListProductReq());
        when(shoppingBagService.getProductsInBagForCurrentCheckout()).thenReturn(productsInBag);

        List<AddAndListProductReq> response = shoppingBagController.getProductsInShoppingBagForCurrentCheckout();

        assertEquals(productsInBag, response);
    }
}
