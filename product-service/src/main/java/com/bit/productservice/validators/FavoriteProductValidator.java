package com.bit.productservice.validators;

import com.bit.productservice.exceptions.productalreadyinfavorite.ProductAlreadyInFavoriteException;
import com.bit.productservice.exceptions.productisnotfavorite.ProductIsNotFavoriteException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;

/**
 * Validator for favorite products.
 */
@Component
public class FavoriteProductValidator {
    /**
     * Checks if a product exists.
     *
     * @param productRepository the ProductRepository object
     * @param productId         the ID of the product
     * @throws ProductNotFoundException if the product is not found
     */
    public void isProductExist(ProductRepository productRepository, String productId){
        if (productRepository.getProductById(productId) == null){
            throw new ProductNotFoundException("Product not found.");
        }
    }

    /**
     * Checks if a product is not in the favorite list.
     *
     * @param productId              the ID of the product
     * @param userCode               the user code
     * @param favoriteProductRepository the FavoriteProductRepository object
     * @throws ProductIsNotFavoriteException if the product is not in the favorite list
     */
    public void isProductNotFavorite(String productId, String userCode, FavoriteProductRepository favoriteProductRepository){
        if (!favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new ProductIsNotFavoriteException("Product is not favorite.");
        }
    }

    /**
     * Checks if a product is already in the favorite list.
     *
     * @param productId              the ID of the product
     * @param userCode               the user code
     * @param favoriteProductRepository the FavoriteProductRepository object
     * @throws ProductAlreadyInFavoriteException if the product is already in the favorite list
     */
    public void isProductFavorite(String productId, String userCode, FavoriteProductRepository favoriteProductRepository){
        if (favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new ProductAlreadyInFavoriteException("Product is already in favorite list");
        }
    }
}
