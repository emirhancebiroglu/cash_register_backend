package bit.salesservice.validators;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.completedcheckout.CompletedCheckoutException;
import bit.salesservice.exceptions.invalidchange.InvalidChangeException;
import bit.salesservice.exceptions.invalidmoneytaken.InvalidMoneyTakenException;
import bit.salesservice.exceptions.invalidpaymentmethod.InvalidPaymentMethodException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckoutValidatorTest {
    @InjectMocks
    private CheckoutValidator checkoutValidator;
    private Checkout checkout;
    private CompleteCheckoutReq completeCheckoutReq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        checkout = new Checkout();
        checkout.setCompleted(false);
        checkout.setProducts(Collections.singletonList(new Product()));

        completeCheckoutReq = new CompleteCheckoutReq();
        completeCheckoutReq.setPaymentMethod("CREDIT_CARD");
    }

    @Test
    void validateMoneyTaken_Success() {
        assertDoesNotThrow(() -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidMoneyTakenException_With_Cash() {
        completeCheckoutReq.setPaymentMethod("CASH");
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidMoneyTakenException_With_Credit_Card() {
        completeCheckoutReq.setPaymentMethod("CREDIT_CARD");
        completeCheckoutReq.setMoneyTaken(25D);
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidChangeException_With_Cash() {
        completeCheckoutReq.setPaymentMethod("CASH");
        completeCheckoutReq.setMoneyTaken(25D);
        assertThrows(InvalidChangeException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidChangeException_With_Credit_Card() {
        completeCheckoutReq.setPaymentMethod("CREDIT_CARD");
        completeCheckoutReq.setChange(25D);
        assertThrows(InvalidChangeException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_CheckoutNotFoundException() {
        assertThrows(CheckoutNotFoundException.class, () -> checkoutValidator.validateCheckout(null, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_ProductNotFoundException() {
        checkout.setProducts(Collections.emptyList());
        assertThrows(ProductNotFoundException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_CompletedCheckoutException() {
        checkout.setCompleted(true);
        assertThrows(CompletedCheckoutException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidPaymentMethodException_WithNullPaymentMethod() {
        completeCheckoutReq.setPaymentMethod(null);
        assertThrows(InvalidPaymentMethodException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidPaymentMethodException_WithInvalidPaymentMethod() {
        completeCheckoutReq.setPaymentMethod("Invalid");
        assertThrows(InvalidPaymentMethodException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }
}
