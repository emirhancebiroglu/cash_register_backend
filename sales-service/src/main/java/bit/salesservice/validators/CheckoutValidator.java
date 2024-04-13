package bit.salesservice.validators;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.PaymentMethod;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.completedcheckout.CompletedCheckoutException;
import bit.salesservice.exceptions.invalidmoneytaken.InvalidMoneyTakenException;
import bit.salesservice.exceptions.invalidpaymentmethod.InvalidPaymentMethodException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CheckoutValidator {
    public void validateCheckout(Checkout checkout, CompleteCheckoutReq completeCheckoutReq) {
        validateCheckoutNotNull(checkout);
        validateProductsNotEmpty(checkout);
        validateCheckoutNotCompleted(checkout);
        validatePaymentMethod(completeCheckoutReq);
        validateMoneyTaken(completeCheckoutReq);
    }

    private void validateMoneyTaken(CompleteCheckoutReq completeCheckoutReq) {
        if (Objects.equals(completeCheckoutReq.getPaymentMethod(), "CASH")){
            if (completeCheckoutReq.getMoneyTaken() == null){
                throw new InvalidMoneyTakenException("You should provide how much money you take from customer with this payment method");
            }
            else if (completeCheckoutReq.getMoneyTaken() <= 0){
                throw new InvalidMoneyTakenException("Money taken cannot be 0 or negative");
            }
        }
        else if(Objects.equals(completeCheckoutReq.getPaymentMethod(), "CREDIT_CARD") && completeCheckoutReq.getMoneyTaken() != null){
            throw new InvalidMoneyTakenException("You should not provide this field with this payment method");
        }
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

    private void validatePaymentMethod(CompleteCheckoutReq completeCheckoutReq) {
        String paymentMethodStr = completeCheckoutReq.getPaymentMethod();

        if (paymentMethodStr == null) {
            throw new InvalidPaymentMethodException("Payment method cannot be null");
        }

        try {
            PaymentMethod.valueOf(paymentMethodStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentMethodException("Invalid payment method");
        }
    }
}
