package bit.salesservice.validators;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BagValidatorTest {
    @InjectMocks
    private BagValidator bagValidator;

    private AddAndListProductReq addAndListProductReq;
    private ProductInfo productInfo;
    private RemoveOrReturnProductFromBagReq removeOrReturnProductFromBagReq;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        addAndListProductReq = new AddAndListProductReq();
        addAndListProductReq.setQuantity(5);

        productInfo = new ProductInfo();
        productInfo.setStockAmount(10);

        removeOrReturnProductFromBagReq = new RemoveOrReturnProductFromBagReq();
        removeOrReturnProductFromBagReq.setQuantity(2);

        product = new Product();
    }

    @Test
    void validationForAddingToBag_ValidInputs() {
        Checkout checkout = new Checkout();
        checkout.setCancelled(false);

        assertDoesNotThrow(() -> bagValidator.validationForAddingToBag(addAndListProductReq, productInfo, checkout));
    }

    @Test
    void validationForAddingToBag_InvalidQuantity() {
        addAndListProductReq.setQuantity(-5);

        assertThrows(InvalidQuantityException.class, validationForAddingToBag());
    }

    @Test
    void validationForAddingToBag_NotEnoughStock() {
        addAndListProductReq.setQuantity(15);

        assertThrows(NotInStocksException.class, validationForAddingToBag());
    }

    @Test
    void validationForAddingToBag_CancelledCheckout() {
        Checkout checkout = new Checkout();
        checkout.setCancelled(true);

        assertThrows(CheckoutNotFoundException.class, () -> bagValidator.validationForAddingToBag(addAndListProductReq, productInfo, checkout));
    }

    @Test
    void validationForRemoveProductFromBag_ValidInputs() {

        assertDoesNotThrow(() -> bagValidator.validationForRemoveProductFromBag(removeOrReturnProductFromBagReq, 5));
    }

    @Test
    void validationForRemoveProductFromBag_InvalidQuantity() {
        removeOrReturnProductFromBagReq.setQuantity(-2);

        assertThrows(InvalidQuantityException.class, () -> bagValidator.validationForRemoveProductFromBag(removeOrReturnProductFromBagReq, 5));
    }

    @Test
    void validationForRemoveProductFromBag_QuantityOutOfRange() {
        removeOrReturnProductFromBagReq.setQuantity(10);

        assertThrows(InvalidQuantityException.class, () -> bagValidator.validationForRemoveProductFromBag(removeOrReturnProductFromBagReq, 5));
    }

    @Test
    void validationForReturnProductFromBag_ValidInputs() {
        removeOrReturnProductFromBagReq.setQuantity(2);

        assertDoesNotThrow(() -> bagValidator.validationForReturnProductFromBag(removeOrReturnProductFromBagReq, product, 5));
    }

    @Test
    void validationForReturnProductFromBag_InvalidQuantity() {
        removeOrReturnProductFromBagReq.setQuantity(-2);

        assertThrows(InvalidQuantityException.class, validationForReturnProductFromBag());
    }

    @Test
    void validationForReturnProductFromBag_ProductRemovedOrReturned() {
        product.setRemoved(true);

        assertThrows(ProductNotFoundException.class, () -> bagValidator.validationForReturnProductFromBag(removeOrReturnProductFromBagReq, product, 5));
    }

    @Test
    void validationForReturnProductFromBag_QuantityOutOfRange() {
        removeOrReturnProductFromBagReq.setQuantity(10);

        assertThrows(InvalidQuantityException.class, () -> bagValidator.validationForReturnProductFromBag(removeOrReturnProductFromBagReq, product, 5));
    }

    @NotNull
    private Executable validationForAddingToBag() {
        return () -> bagValidator.validationForAddingToBag(addAndListProductReq, productInfo, new Checkout());
    }

    @NotNull
    private Executable validationForReturnProductFromBag() {
        return () -> bagValidator.validationForReturnProductFromBag(removeOrReturnProductFromBagReq, new Product(), 5);
    }
}
