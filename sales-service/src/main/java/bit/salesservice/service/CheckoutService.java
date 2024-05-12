package bit.salesservice.service;

import bit.salesservice.dto.CompleteCheckoutReq;

/**
 * Service interface for managing checkout operations.
 */
public interface CheckoutService {
    /**
     * Cancels a checkout.
     *
     * @param checkoutId the ID of the checkout to be canceled
     */
    void cancelCheckout(Long checkoutId);

    /**
     * Completes a checkout.
     *
     * @param completeCheckoutReq the request containing the details to complete the checkout
     * @param checkoutId
     */
    void completeCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId);

    void openSale();
}
