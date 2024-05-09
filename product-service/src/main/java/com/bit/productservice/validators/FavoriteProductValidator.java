package com.bit.productservice.validators;

import com.bit.productservice.exceptions.productalreadyinfavorite.ProductAlreadyInFavoriteException;
import com.bit.productservice.exceptions.productisnotfavorite.ProductIsNotFavoriteException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Validator for favorite products.
 */
@Component
public class FavoriteProductValidator {
    private static final Logger logger = LogManager.getLogger(FavoriteProductValidator.class);


    /**
     * Checks if a product exists.
     *
     * @param productRepository the ProductRepository object
     * @param productId         the ID of the product
     * @throws ProductNotFoundException if the product is not found
     */
    public void isProductExist(ProductRepository productRepository, String productId){
        if (productRepository.getProductById(productId) == null){
            logger.error("Product not found");
            throw new ProductNotFoundException("Product not found.");
        }
    }

    /**
     * Checks if a product is not in the favorite list.
     *
     * @param productId              the ID of the product
     * @param userId               the user id
     * @param favoriteProductRepository the FavoriteProductRepository object
     * @throws ProductIsNotFavoriteException if the product is not in the favorite list
     */
    public void isProductNotFavorite(String productId, Long userId, FavoriteProductRepository favoriteProductRepository){
        if (!favoriteProductRepository.existsByUserIdAndProductId(userId, productId)) {
            logger.error("Product is not favorite");
            throw new ProductIsNotFavoriteException("Product is not favorite.");
        }
    }

    /**
     * Checks if a product is already in the favorite list.
     *
     * @param productId              the ID of the product
     * @param userId                 the user id
     * @param favoriteProductRepository the FavoriteProductRepository object
     * @throws ProductAlreadyInFavoriteException if the product is already in the favorite list
     */
    public void isProductFavorite(String productId, Long userId, FavoriteProductRepository favoriteProductRepository){
        if (favoriteProductRepository.existsByUserIdAndProductId(userId, productId)) {
            logger.error("Product is already in favorite list");
            throw new ProductAlreadyInFavoriteException("Product is already in favorite list");
        }
    }
}
