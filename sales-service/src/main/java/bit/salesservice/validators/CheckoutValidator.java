package bit.salesservice.validators;

import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.PaymentMethod;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.completedcheckout.CompletedCheckoutException;
import bit.salesservice.exceptions.invalidpaymentmethod.InvalidPaymentMethodException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CheckoutValidator {
    public void validateCheckout(Checkout checkout, String paymentMethodStr) {
        validateCheckoutNotNull(checkout);
        validateProductsNotEmpty(checkout);
        validateCheckoutNotCompleted(checkout);
        validatePaymentMethod(paymentMethodStr);
    }

    private void validateCheckoutNotNull(Checkout checkout) {
        if (checkout == null) {
            throw new CheckoutNotFoundException("Checkout not found");
        }
    }

    private void validateProductsNotEmpty(Checkout checkout) {
        if (checkout.getProducts().isEmpty()) {
            throw new ProductNotFoundException("No products in the checkout");
        }
    }

    private void validateCheckoutNotCompleted(Checkout checkout) {
        if (checkout.isCompleted()) {
            throw new CompletedCheckoutException("Checkout already completed");
        }
    }

    private void validatePaymentMethod(String paymentMethodStr) {
        if (paymentMethodStr == null) {
            throw new InvalidPaymentMethodException("Payment method cannot be null");
        }

        try {
            PaymentMethod.valueOf(paymentMethodStr);
        } catch (InvalidPaymentMethodException e) {
            throw new InvalidPaymentMethodException("Invalid payment method");
        }
    }
}
