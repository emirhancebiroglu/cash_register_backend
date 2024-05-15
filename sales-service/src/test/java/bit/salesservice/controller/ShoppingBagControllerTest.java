package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
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

    private RemoveOrReturnProductFromBagReq req;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        req = new RemoveOrReturnProductFromBagReq();
        req.setCheckoutId(1L);
        req.setCode("test");
        req.setQuantity(2);
    }

    @Test
    void addProductToShoppingBag_Success() {
        AddAndListProductReq request = new AddAndListProductReq();

        doNothing().when(shoppingBagService).addProductToBag(request, 1L);

        ResponseEntity<String> response = shoppingBagController.addProductToShoppingBag(request, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product added successfully", response.getBody());
    }

    @Test
    void removeProductFromShoppingBag_Success() {
        doNothing().when(shoppingBagService).removeProductFromBag(req);

        ResponseEntity<String> response = shoppingBagController.removeProductFromShoppingBag(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product removed successfully", response.getBody());
    }

    @Test
    void returnProductFromShoppingBag_Success() {
        doNothing().when(shoppingBagService).returnProductFromBag(req);

        ResponseEntity<String> response = shoppingBagController.returnProductFromShoppingBag(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product returned successfully", response.getBody());
    }

    @Test
    void removeAll_Success() {
        doNothing().when(shoppingBagService).removeAll(1L);

        ResponseEntity<String> response = shoppingBagController.removeAll(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bag is cleaned successfully", response.getBody());
    }

    @Test
    void getProductsInShoppingBagForCurrentCheckout_Success() {
        List<AddAndListProductReq> productsInBag = List.of(new AddAndListProductReq(), new AddAndListProductReq());
        when(shoppingBagService.getProductsInBag(1L)).thenReturn(productsInBag);

        List<AddAndListProductReq> response = shoppingBagController.getProductsInShoppingBag(1L);

        assertEquals(productsInBag, response);
    }
}
