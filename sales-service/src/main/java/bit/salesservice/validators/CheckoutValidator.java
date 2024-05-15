package bit.salesservice.validators;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.completedcheckout.CompletedCheckoutException;
import bit.salesservice.exceptions.invalidmoneytaken.InvalidMoneyTakenException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Validates the checkout process by ensuring that necessary conditions are met before completing the checkout.
 */
@Component
public class CheckoutValidator {
    private static final Logger logger = LogManager.getLogger(CheckoutValidator.class);

    /**
     * Validates the checkout process by ensuring that necessary conditions are met before completing the checkout.
     *
     * @param checkout           The checkout object to be validated.
     * @param completeCheckoutReq The request containing details of the checkout to be completed.
     * @throws CheckoutNotFoundException    if the checkout object is null.
     * @throws ProductNotFoundException     if there are no products in the checkout.
     * @throws CompletedCheckoutException    if the checkout is already completed.
     * @throws InvalidMoneyTakenException    if the money taken is not provided for cash payment or is negative.
     */
    public void validateCheckout(Checkout checkout, CompleteCheckoutReq completeCheckoutReq) {
        validateProductsNotEmpty(checkout);
        validateCheckoutNotCompleted(checkout);
        validateMoneyTaken(completeCheckoutReq, checkout);
    }

    /**
     * Validates whether money taken is provided for cash payment and is not negative.
     *
     * @param completeCheckoutReq The request containing details of the checkout to be completed.
     * @throws InvalidMoneyTakenException if money taken is not provided for cash payment or is negative.
     */
    private void validateMoneyTaken(CompleteCheckoutReq completeCheckoutReq, Checkout checkout) {
        Double moneyTakenFromCash = completeCheckoutReq.getMoneyTakenFromCash();
        Double moneyTakenFromCard = completeCheckoutReq.getMoneyTakenFromCard();
        Double totalPrice = checkout.getTotalPrice();

        if ((moneyTakenFromCash == null || moneyTakenFromCash == 0D) &&
                (moneyTakenFromCard == null || moneyTakenFromCard == 0D)) {
            logger.error("Payment required");
            throw new InvalidMoneyTakenException("Payment required");
        }

        if (moneyTakenFromCash != null && moneyTakenFromCard == null && (moneyTakenFromCash < totalPrice)) {
                logger.error("Money taken from cash cannot be less than total price: {}", totalPrice);
                throw new InvalidMoneyTakenException("Money taken from cash cannot be less than total price: " + totalPrice);

        }

        if (moneyTakenFromCard != null && moneyTakenFromCash == null && (!Objects.equals(moneyTakenFromCard, totalPrice))) {
                logger.error("Money taken from card should be equal to total price: {}", totalPrice);
                throw new InvalidMoneyTakenException("Money taken from card should be equal to total price: " + totalPrice);

        }

        if (moneyTakenFromCard != null && moneyTakenFromCash != null) {
            if (moneyTakenFromCard + moneyTakenFromCash < totalPrice || moneyTakenFromCard <= 0D || moneyTakenFromCash <= 0D) {
                logger.error("Money taken from cash and card cannot be less than total price: {}", totalPrice);
                throw new InvalidMoneyTakenException("Money taken from cash and card cannot be less than total price: " + totalPrice);
            }

            if (moneyTakenFromCard > totalPrice) {
                logger.error("Money taken from card cannot be greater than total price: {}", totalPrice);
                throw new InvalidMoneyTakenException("Money taken from card cannot be greater than total price: " + totalPrice);
            }
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
            logger.error("No products in the checkout: {}", checkout.getProducts());
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
            logger.error("Checkout already completed: {}", checkout);
            throw new CompletedCheckoutException("Checkout already completed");
        }

        if (checkout.isCancelled()){
            throw new CheckoutNotFoundException("Checkout is cancelled");
        }
    }
}
