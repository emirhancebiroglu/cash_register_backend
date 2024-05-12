package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.service.ShoppingBagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing shopping bag operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cashier")
public class ShoppingBagController {
    private final ShoppingBagService shoppingBagService;

    /**
     * Endpoint for adding a product to the shopping bag.
     *
     * @param req the request body containing the details of the product to be added
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/add-product/{checkoutId}")
    public ResponseEntity<String> addProductToShoppingBag(@RequestBody AddAndListProductReq req, @PathVariable Long checkoutId) {
        shoppingBagService.addProductToBag(req, checkoutId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    /**
     * Endpoint for removing a product from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/remove-product")
    public ResponseEntity<String> removeProductFromShoppingBag(@RequestBody RemoveOrReturnProductFromBagReq request) {
        shoppingBagService.removeProductFromBag(request);
        return ResponseEntity.status(HttpStatus.OK).body("Product removed successfully");
    }

    /**
     * Endpoint for returning a product from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/return-product")
    public ResponseEntity<String> returnProductFromShoppingBag(@RequestBody RemoveOrReturnProductFromBagReq request) {
        shoppingBagService.returnProductFromBag(request);
        return ResponseEntity.status(HttpStatus.OK).body("Product returned successfully");
    }

    /**
     * Endpoint for removing all products from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/remove-all/{checkoutId}")
    public ResponseEntity<String> removeAll(@PathVariable Long checkoutId) {
        shoppingBagService.removeAll(checkoutId);
        return ResponseEntity.status(HttpStatus.OK).body("Bag is cleaned successfully");
    }

    /**
     * Endpoint for retrieving all products in the shopping bag for the current checkout.
     *
     * @return a list of AddAndListProductReq containing the products in the shopping bag
     */
    @GetMapping("/bag/get-products/{checkoutId}")
    public List<AddAndListProductReq> getProductsInShoppingBag(@PathVariable Long checkoutId) {
        return shoppingBagService.getProductsInBag(checkoutId);
    }
}