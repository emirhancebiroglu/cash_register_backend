package bit.salesservice.controller;

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
    public ResponseEntity<String> completeCheckout(@RequestParam(required = false) String paymentMethodStr) {
        checkoutService.completeCheckout(paymentMethodStr);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }

    @PostMapping("/cancel-checkout/{checkoutId}")
    public ResponseEntity<String> cancelCheckout(@PathVariable Long checkoutId) {
        checkoutService.cancelCheckout(checkoutId);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}