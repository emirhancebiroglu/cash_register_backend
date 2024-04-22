package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;

import java.util.List;

/**
 * Interface for favorite product service.
 */
public interface FavoriteProductService {
    /**
     * Adds a product to favorites.
     *
     * @param productId the ID of the product to add to favorites
     */
    void addProductToFavorites(String productId);

    /**
     * Lists favorite products for the current user.
     *
     * @param pageNo   the page number
     * @param pageSize the size of each page
     * @return a list of favorite products
     */
    List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize);

    /**
     * Removes a product from favorites.
     *
     * @param productId the ID of the product to remove from favorites
     */
    void removeProductFromFavorites(String productId);
}
