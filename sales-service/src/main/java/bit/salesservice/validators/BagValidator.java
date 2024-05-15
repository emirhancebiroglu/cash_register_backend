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

@Component
@RequiredArgsConstructor
public class BagValidator {
    private static final Logger logger = LogManager.getLogger(BagValidator.class);
    private static final String INVALID_QUANTITY = "Please provide a valid quantity";
    private static final String NOT_IN_STOCKS = "There is not enough product in stocks.";
    private static final String CHECKOUT_NOT_FOUND = "Checkout not found";
    private static final String OUT_OF_RANGE = "Quantity is out of range";


    public void validationForAddingToBag(AddAndListProductReq req, ProductInfo productInfo, Checkout checkout) {
        if (req.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (productInfo.getStockAmount() < req.getQuantity()){
            logger.error(NOT_IN_STOCKS);
            throw new NotInStocksException(NOT_IN_STOCKS);
        }


        if (checkout.isCancelled()){
            logger.error(CHECKOUT_NOT_FOUND);
            throw new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
        }
    }

    public void validationForRemoveProductFromBag(RemoveOrReturnProductFromBagReq request, int productQuantity) {
        if (request.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (productQuantity < request.getQuantity()){
            logger.error(OUT_OF_RANGE);
            throw new InvalidQuantityException(OUT_OF_RANGE);
        }
    }

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
