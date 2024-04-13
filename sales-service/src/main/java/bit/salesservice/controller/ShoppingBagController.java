package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.service.ShoppingBagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cashier")
public class ShoppingBagController {
    private final ShoppingBagService shoppingBagService;

    @PostMapping("/bag/add-product")
    public ResponseEntity<String> addProductToShoppingBag(@RequestBody AddAndListProductReq req) {
        shoppingBagService.addProductToBag(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    @PostMapping("/bag/remove-product/{id}/{quantity}")
    public ResponseEntity<String> removeProductFromShoppingBag(@PathVariable Long id, @PathVariable Integer quantity) {
        shoppingBagService.removeProductFromBag(id, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Product removed successfully");
    }

    @PostMapping("/bag/return-product/{id}/{quantity}")
    public ResponseEntity<String> returnProductFromShoppingBag(@PathVariable Long id, @PathVariable Integer quantity) {
        shoppingBagService.returnProductFromBag(id, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("Product returned successfully");

    }

    @PostMapping("/bag/remove-all")
    public ResponseEntity<String> removeAll() {
        shoppingBagService.removeAll();
        return ResponseEntity.status(HttpStatus.OK).body("Bag is cleaned successfully");
    }

    @GetMapping("/bag/get-products")
    public List<AddAndListProductReq> getProductsInShoppingBagForCurrentCheckout() {
        return shoppingBagService.getProductsInBagForCurrentCheckout();
    }
}