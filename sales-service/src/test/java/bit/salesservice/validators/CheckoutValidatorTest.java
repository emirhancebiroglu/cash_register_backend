package bit.salesservice.validators;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.completedcheckout.CompletedCheckoutException;
import bit.salesservice.exceptions.invalidmoneytaken.InvalidMoneyTakenException;
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
        checkout.setTotalPrice(20D);

        completeCheckoutReq = new CompleteCheckoutReq();
        completeCheckoutReq.setMoneyTakenFromCard(11D);
        completeCheckoutReq.setMoneyTakenFromCash(11D);
    }

    @Test
    void validateMoneyTaken_Success() {
        assertDoesNotThrow(() -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidMoneyTakenException_With_Cash_NullField() {
        completeCheckoutReq.setMoneyTakenFromCash(0D);
        completeCheckoutReq.setMoneyTakenFromCard(0D);
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidMoneyTakenException_With_Cash_ZeroOrNegativeField() {
        completeCheckoutReq.setMoneyTakenFromCard(null);
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_InvalidMoneyTakenException_With_Credit_Card() {
        completeCheckoutReq.setMoneyTakenFromCash(null);
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }

    @Test
    void validateMoneyTaken_CheckoutNotFoundException() {
        completeCheckoutReq.setMoneyTakenFromCard(5D);
        completeCheckoutReq.setMoneyTakenFromCash(5D);
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
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
    void validateMoneyTaken_InvalidPaymentMethodException_WithInvalidPaymentMethod() {
        completeCheckoutReq.setMoneyTakenFromCard(22D);
        assertThrows(InvalidMoneyTakenException.class, () -> checkoutValidator.validateCheckout(checkout, completeCheckoutReq));
    }
}