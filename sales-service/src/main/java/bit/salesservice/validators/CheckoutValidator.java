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

/**
 * Validates the checkout process by ensuring that necessary conditions are met before completing the checkout.
 */
@Component
public class CheckoutValidator {
    /**
     * Validates the checkout process by ensuring that necessary conditions are met before completing the checkout.
     *
     * @param checkout           The checkout object to be validated.
     * @param completeCheckoutReq The request containing details of the checkout to be completed.
     * @throws CheckoutNotFoundException    if the checkout object is null.
     * @throws ProductNotFoundException     if there are no products in the checkout.
     * @throws CompletedCheckoutException    if the checkout is already completed.
     * @throws InvalidPaymentMethodException if the payment method is null or invalid.
     * @throws InvalidMoneyTakenException    if the money taken is not provided for cash payment or is negative.
     */
    public void validateCheckout(Checkout checkout, CompleteCheckoutReq completeCheckoutReq) {
        validateCheckoutNotNull(checkout);
        validateProductsNotEmpty(checkout);
        validateCheckoutNotCompleted(checkout);
        validatePaymentMethod(completeCheckoutReq);
        validateMoneyTaken(completeCheckoutReq, checkout);
    }

    /**
     * Validates whether money taken is provided for cash payment and is not negative.
     *
     * @param completeCheckoutReq The request containing details of the checkout to be completed.
     * @throws InvalidMoneyTakenException if money taken is not provided for cash payment or is negative.
     */
    private void validateMoneyTaken(CompleteCheckoutReq completeCheckoutReq, Checkout checkout) {
        if (Objects.equals(completeCheckoutReq.getPaymentMethod(), "CASH")){
            if (completeCheckoutReq.getMoneyTaken() == null){
                throw new InvalidMoneyTakenException("You should provide how much money you take from customer with this payment method");
            }
            else if (completeCheckoutReq.getMoneyTaken() <= checkout.getTotalPrice()){
                throw new InvalidMoneyTakenException("Money taken cannot be less than total price");
            }
        }
        else if(Objects.equals(completeCheckoutReq.getPaymentMethod(), "CREDIT_CARD") && completeCheckoutReq.getMoneyTaken() != null){
            throw new InvalidMoneyTakenException("You should not provide this field with this payment method");
        }
    }

    /**
     * Validates whether the checkout object is not null.
     *
     * @param checkout The checkout object to be validated.
     * @throws CheckoutNotFoundException if the checkout object is null.
     */
    private void validateCheckoutNotNull(Checkout checkout) {
        if (checkout == null) {
            throw new CheckoutNotFoundException("Checkout not found");
        }
    }

    /**
     * Validates whether there are products in the checkout.
     *
     * @param checkout The checkout object containing products.
     * @throws ProductNotFoundException if there are no products in the checkout.
     */
    private void validateProductsNotEmpty(Checkout checkout) {
        if (checkout.getProducts().isEmpty()) {
            throw new ProductNotFoundException("No products in the checkout");
        }
    }

    /**
     * Validates whether the checkout is not already completed.
     *
     * @param checkout The checkout object to be validated.
     * @throws CompletedCheckoutException if the checkout is already completed.
     */
    private void validateCheckoutNotCompleted(Checkout checkout) {
        if (checkout.isCompleted()) {
            throw new CompletedCheckoutException("Checkout already completed");
        }
    }

    /**
     * Validates whether the payment method is not null or invalid.
     *
     * @param completeCheckoutReq The request containing details of the checkout to be completed.
     * @throws InvalidPaymentMethodException if the payment method is null or invalid.
     */
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
