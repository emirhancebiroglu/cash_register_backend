package bit.salesservice.service;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;

import java.util.List;

/**
 * Service interface for managing shopping bag operations.
 */
public interface ShoppingBagService {
    /**
     * Adds a product to the shopping bag.
     *
     * @param req        the request containing the product details to be added
     */
    void addProductToBag(AddAndListProductReq req, Long checkoutId);

    /**
     * Removes a specified quantity of a product from the shopping bag.
     *
     */
    void removeProductFromBag(RemoveOrReturnProductFromBagReq request);

    /**
     * Removes all products from the shopping bag.
     */
    void removeAll(Long checkoutId);

    /**
     * Retrieves a list of products in the shopping bag for the current checkout.
     *
     * @return a list of products in the shopping bag
     */
    List<AddAndListProductReq> getProductsInBag(Long checkoutId);

    /**
     * Returns a specified quantity of a product from the shopping bag.
     *
     */
    void returnProductFromBag(RemoveOrReturnProductFromBagReq request);
}
