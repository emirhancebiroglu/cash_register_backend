package bit.salesservice.service;

public interface CheckoutService {
    void cancelCheckout(Long checkoutId);
    void completeCheckout(String paymentMethodStr);
}
