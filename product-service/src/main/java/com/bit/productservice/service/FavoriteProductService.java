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
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param searchTerm the search term to filter products
     * @param stockStatus the stock status to filter products (e.g., "IN_STOCK", "OUT_OF_STOCK")
     * @param sortBy the field to sort the products by
     * @param sortOrder the order to sort the products (e.g., "ASC", "DESC")
     * @return a list of {@link ProductDTO} objects representing the favorite products
     */
    List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize, String searchTerm, String stockStatus, String sortBy, String sortOrder);

    /**
     * Removes a product from favorites.
     *
     * @param productId the ID of the product to remove from favorites
     */
    void removeProductFromFavorites(String productId);
}
