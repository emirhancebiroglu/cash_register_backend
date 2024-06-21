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
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for validating the operations related to the shopping bag.
 * It checks for various conditions such as quantity, stock availability, and checkout status.
 */
@Component
@RequiredArgsConstructor
public class BagValidator {
    private static final Logger logger = LogManager.getLogger(BagValidator.class);
    private static final String INVALID_QUANTITY = "Please provide a valid quantity";
    private static final String NOT_IN_STOCKS = "There is not enough product in stocks.";
    private static final String CHECKOUT_NOT_FOUND = "Checkout not found";
    private static final String OUT_OF_RANGE = "Quantity is out of range";


    /**
     * Validates the request for adding a product to the shopping bag.
     *
     * @param req The request object containing the product and quantity information.
     * @param productInfo The product information object.
     * @param checkout The checkout object associated with the shopping bag.
     * @throws InvalidQuantityException If the quantity is not valid.
     * @throws NotInStocksException If the product is out of stock.
     * @throws CheckoutNotFoundException If the checkout is not found or completed.
     */
    public void validationForAddingToBag(AddAndListProductReq req, ProductInfo productInfo, Checkout checkout) {
        if (req.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (productInfo.getStockAmount() < req.getQuantity()){
            logger.error(NOT_IN_STOCKS);
            throw new NotInStocksException(NOT_IN_STOCKS);
        }

        if (checkout.isCancelled() || checkout.isCompleted()){
            logger.error(CHECKOUT_NOT_FOUND);
            throw new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
        }
    }

    /**
     * Validates the request for removing a product from the shopping bag.
     *
     * @param request The request object containing the product and quantity information.
     * @param productQuantity The current quantity of the product in the shopping bag.
     * @param checkout The checkout object associated with the shopping bag.
     *
     * @throws InvalidQuantityException If the quantity to be removed is not valid or exceeds the available quantity.
     * @throws CheckoutNotFoundException If the checkout is not found or has been cancelled or completed.
     */
    public void validationForRemoveProductFromBag(RemoveOrReturnProductFromBagReq request, int productQuantity, Checkout checkout) {
        if (request.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (productQuantity < request.getQuantity()){
            logger.error(OUT_OF_RANGE);
            throw new InvalidQuantityException(OUT_OF_RANGE);
        }

        if (checkout.isCancelled() || checkout.isCompleted()){
            logger.error(CHECKOUT_NOT_FOUND);
            throw new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
        }
    }

    /**
     * Validates the request for returning a product from the shopping bag.
     *
     * @param request The request object containing the product and quantity information.
     * @param product The product entity to be returned.
     * @param productQuantity The current quantity of the product in the shopping bag.
     *
     * @throws InvalidQuantityException If the quantity to be returned is not valid or exceeds the available quantity.
     * @throws ProductNotFoundException If the product is already removed or returned.
     */
    public void validationForReturnProductFromBag(RemoveOrReturnProductFromBagReq request, Product product, int productQuantity) {
        if (request.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (product.isRemoved() || product.isReturned()){
            logger.error("This product is removed or returned");
            throw new ProductNotFoundException("This product is removed or returned");
        }

        if (productQuantity < request.getQuantity()) {
            logger.error(OUT_OF_RANGE);
            throw new InvalidQuantityException(OUT_OF_RANGE);
        }
    }
}