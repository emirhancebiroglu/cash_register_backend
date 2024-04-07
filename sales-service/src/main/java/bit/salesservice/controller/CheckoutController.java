package bit.salesservice.controller;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cashier")
public class CheckoutController {
    private final CheckoutService checkoutService;
    @PostMapping("/complete-checkout")
    public ResponseEntity<String> completeCheckout(@RequestBody CompleteCheckoutReq completeCheckoutReq) {
        checkoutService.completeCheckout(completeCheckoutReq);
        return ResponseEntity.status(HttpStatus.OK).body("Checkout completed successfully");
    }

    @PostMapping("/cancel-checkout/{checkoutId}")
    public ResponseEntity<String> cancelCheckout(@PathVariable Long checkoutId) {
        checkoutService.cancelCheckout(checkoutId);
        return ResponseEntity.status(HttpStatus.OK).body("Checkout canceled successfully");
    }
}