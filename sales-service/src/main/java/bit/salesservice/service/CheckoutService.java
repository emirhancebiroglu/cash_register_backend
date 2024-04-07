package bit.salesservice.service;

import bit.salesservice.dto.CompleteCheckoutReq;

public interface CheckoutService {
    void cancelCheckout(Long checkoutId);
    void completeCheckout(CompleteCheckoutReq completeCheckoutReq);
}
