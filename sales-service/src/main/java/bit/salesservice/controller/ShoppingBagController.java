package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
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
    @PostMapping("/bag/add-product")
    public ResponseEntity<String> addProductToShoppingBag(@RequestBody AddAndListProductReq req) {
        shoppingBagService.addProductToBag(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    /**
     * Endpoint for removing a product from the shopping bag.
     *
     * @param id       the ID of the product to be removed
     * @param quantity the quantity of the product to be removed
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/remove-product/{id}/{quantity}")
    public ResponseEntity<String> removeProductFromShoppingBag(@PathVariable Long id, @PathVariable Integer quantity) {
        shoppingBagService.removeProductFromBag(id, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Product removed successfully");
    }

    /**
     * Endpoint for returning a product from the shopping bag.
     *
     * @param id       the ID of the product to be returned
     * @param quantity the quantity of the product to be returned
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/return-product/{id}/{quantity}")
    public ResponseEntity<String> returnProductFromShoppingBag(@PathVariable Long id, @PathVariable Integer quantity) {
        shoppingBagService.returnProductFromBag(id, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Product returned successfully");

    }

    /**
     * Endpoint for removing all products from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/remove-all")
    public ResponseEntity<String> removeAll() {
        shoppingBagService.removeAll();
        return ResponseEntity.status(HttpStatus.OK).body("Bag is cleaned successfully");
    }

    /**
     * Endpoint for retrieving all products in the shopping bag for the current checkout.
     *
     * @return a list of AddAndListProductReq containing the products in the shopping bag
     */
    @GetMapping("/bag/get-products")
    public List<AddAndListProductReq> getProductsInShoppingBagForCurrentCheckout() {
        return shoppingBagService.getProductsInBagForCurrentCheckout();
    }
}