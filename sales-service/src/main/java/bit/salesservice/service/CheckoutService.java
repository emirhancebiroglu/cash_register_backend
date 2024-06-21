package bit.salesservice.service;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;

import java.util.List;

/**
 * Service interface for managing checkout operations.
 */
public interface CheckoutService {
    /**
     * Cancels the checkout process.
     *
     * @param checkoutId The unique identifier of the checkout to be cancelled.
     * @throws CheckoutNotFoundException If the checkout with the given id does not exist.
     */
    void cancelCheckout(Long checkoutId);

    /**
     * Performs the checkout process.
     *
     * @param completeCheckoutReq The request containing the details of the checkout.
     * @param checkoutId The ID of the checkout to be completed.
     * @throws CheckoutNotFoundException If the checkout with the given ID is not found.
     */
    void completeCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId);

    /**
     * This method creates a new checkout and adds products to the shopping bag.
     *
     * @param reqs A list of product requests containing the product code and quantity.
     * @throws ProductNotFoundException If the product with the given code does not exist.
     * @throws NotInStocksException If the quantity of the product is greater than the available stock.
     * @throws CheckoutNotFoundException If the checkout with the given id does not exist.
     */
    void createCheckout(List<AddAndListProductReq> reqs);

    /**
     * Adds products to the shopping bag associated with the given checkout ID.
     *
     * @param reqs A list of product requests containing the product code and quantity.
     * @param checkoutId The ID of the checkout associated with the shopping bag.
     *
     * @throws CheckoutNotFoundException If the checkout associated with the given checkout ID is not found.
     * @throws NotInStocksException If the quantity of a product requested exceeds the available stock.
     * @throws ProductNotFoundException If the product associated with the given code is not found.
     */
    void addProductsToBag(List<AddAndListProductReq> reqs, Long checkoutId);

    /**
     * Removes products from the shopping bag.
     *
     * @param reqs A list of product requests containing the product code and quantity to be removed.
     * @param checkoutId The ID of the checkout associated with the products.
     *
     * @throws CheckoutNotFoundException If the checkout associated with the checkoutId is not found.
     * @throws ProductNotFoundException If the product associated with the product code in the request is not found.
     * @throws UncompletedCheckoutException If the checkout is not completed.
     */
    void removeProductsFromBag(List<RemoveOrReturnProductFromBagReq> reqs, Long checkoutId);

    /**
     * This method handles the process of returning a product from the shopping bag.
     * It checks if the checkout is completed, validates the returning product, updates the product quantity,
     * and sends the returned products information to the reporting service.
     *
     * @param request The request object containing the product code and quantity to be returned.
     * @param checkoutId The ID of the checkout associated with the product.
     * @throws UncompletedCheckoutException If the checkout is not completed.
     */
    void returnProductFromBag(RemoveOrReturnProductFromBagReq request, Long checkoutId);

    /**
     * Removes all products from the shopping bag associated with the given checkout.
     *
     * @param checkoutId The unique identifier of the checkout.
     * @throws CheckoutNotFoundException If the checkout with the given checkoutId is not found.
     * @throws ProductNotFoundException If any product associated with the checkout is not found.
     */
    void removeAll(Long checkoutId);

    /**
     * Retrieves the list of products in the shopping bag for the given checkout.
     *
     * @param checkoutId The unique identifier of the checkout.
     * @return A list of {@link AddAndListProductReq} representing the products in the shopping bag.
     * @throws CheckoutNotFoundException If the checkout with the given checkoutId is not found.
     */
    List<AddAndListProductReq> getProductsInBag(Long checkoutId);
}