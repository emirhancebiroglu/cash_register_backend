package bit.salesservice.controller;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        checkoutService.completeCheckout(completeCheckoutReq, checkoutId);
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
        checkoutService.cancelCheckout(checkoutId);
        return ResponseEntity.status(HttpStatus.OK).body("Checkout canceled successfully");
    }

    @PostMapping("/open-sale")
    public ResponseEntity<String> openSale() {
        checkoutService.openSale();
        return ResponseEntity.status(HttpStatus.OK).body("Sale opened successfully");
    }
}