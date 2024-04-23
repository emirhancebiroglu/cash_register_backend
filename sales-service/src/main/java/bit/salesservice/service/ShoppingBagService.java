package bit.salesservice.service;

import bit.salesservice.dto.AddAndListProductReq;

import java.util.List;

/**
 * Service interface for managing shopping bag operations.
 */
public interface ShoppingBagService {
    /**
     * Adds a product to the shopping bag.
     *
     * @param req the request containing the product details to be added
     */
    void addProductToBag(AddAndListProductReq req);

    /**
     * Removes a specified quantity of a product from the shopping bag.
     *
     * @param id       the ID of the product to be removed
     * @param quantity the quantity of the product to be removed
     */
    void removeProductFromBag(Long id, Integer quantity);

    /**
     * Removes all products from the shopping bag.
     */
    void removeAll();

    /**
     * Retrieves a list of products in the shopping bag for the current checkout.
     *
     * @return a list of products in the shopping bag
     */
    List<AddAndListProductReq> getProductsInBagForCurrentCheckout();

    /**
     * Returns a specified quantity of a product from the shopping bag.
     *
     * @param id       the ID of the product to be returned
     * @param quantity the quantity of the product to be returned
     */
    void returnProductFromBag(Long id, Integer quantity);
}
