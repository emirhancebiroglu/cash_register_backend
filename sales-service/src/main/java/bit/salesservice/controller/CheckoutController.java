package bit.salesservice.controller;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing checkout operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cashier")
public class CheckoutController {
    private final CheckoutService checkoutService;

    /**
     * Endpoint for completing a checkout process.
     *
     * @param completeCheckoutReq the request body containing the details required to complete the checkout
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/complete-checkout/{checkoutId}")
    public ResponseEntity<String> completeCheckout(@RequestBody CompleteCheckoutReq completeCheckoutReq, @PathVariable Long checkoutId) {
        // Call the checkout service to complete the checkout process
        checkoutService.completeCheckout(completeCheckoutReq, checkoutId);

        // Return response with status code 200 (OK) and a success message
        return ResponseEntity.status(HttpStatus.OK).body("Checkout completed successfully");
    }

    /**
     * Endpoint for canceling a checkout process.
     *
     * @param checkoutId the ID of the checkout to be canceled
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/cancel-checkout/{checkoutId}")
    public ResponseEntity<String> cancelCheckout(@PathVariable Long checkoutId) {
        // Call the checkout service to cancel the checkout process
        checkoutService.cancelCheckout(checkoutId);

        // Return response with status code 200 (OK) and a success message
        return ResponseEntity.status(HttpStatus.OK).body("Checkout canceled successfully");
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<String> createCheckout(@RequestBody List<AddAndListProductReq> reqs) {
        // Call the checkout service to open a sale
        checkoutService.createCheckout(reqs);

        // Return response with status code 200 (OK) and a success message
        return ResponseEntity.status(HttpStatus.OK).body("Checkout created successfully");
    }

    /**
     * Endpoint for adding a product to the shopping bag.
     *
     * @param reqs the request body containing the details of the product to be added
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/add-products/{checkoutId}")
    public ResponseEntity<String> addProductsToShoppingBag(@RequestBody List<AddAndListProductReq> reqs, @PathVariable Long checkoutId) {
        // Call the shopping bag service to add a product to the bag
        checkoutService.addProductsToBag(reqs, checkoutId);

        // Return response with status code 201 (CREATED) and a success message
        return ResponseEntity.status(HttpStatus.CREATED).body("Products added successfully");
    }

    /**
     * Endpoint for removing a product from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/remove-products/{checkoutId}")
    public ResponseEntity<String> removeProductsFromShoppingBag(@RequestBody List<RemoveOrReturnProductFromBagReq> reqs, @PathVariable Long checkoutId) {
        // Call the shopping bag service to remove a product from the bag
        checkoutService.removeProductsFromBag(reqs, checkoutId);

        // Return response with status code 200 (OK) and a success message
        return ResponseEntity.status(HttpStatus.OK).body("Products removed successfully");
    }

    /**
     * Endpoint for returning a product from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/return-product/{checkoutId}")
    public ResponseEntity<String> returnProductFromShoppingBag(@RequestBody RemoveOrReturnProductFromBagReq request, @PathVariable Long checkoutId) {
        // Call the shopping bag service to return a product to the bag
        checkoutService.returnProductFromBag(request, checkoutId);

        // Return response with status code 200 (OK) and a success message
        return ResponseEntity.status(HttpStatus.OK).body("Product returned successfully");
    }

    /**
     * Endpoint for removing all products from the shopping bag.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping("/bag/remove-all/{checkoutId}")
    public ResponseEntity<String> removeAll(@PathVariable Long checkoutId) {
        // Call the shopping bag service to remove all products from the bag
        checkoutService.removeAll(checkoutId);

        // Return response with status code 200 (OK) and a success message
        return ResponseEntity.status(HttpStatus.OK).body("Bag is cleaned successfully");
    }

    /**
     * Endpoint for retrieving all products in the shopping bag for the current checkout.
     *
     * @return a list of AddAndListProductReq containing the products in the shopping bag
     */
    @GetMapping("/bag/get-products/{checkoutId}")
    public List<AddAndListProductReq> getProductsInBag(@PathVariable Long checkoutId) {
        // Call the shopping bag service to retrieve products in the bag
        return checkoutService.getProductsInBag(checkoutId);
    }
}