package com.bit.productservice.validators;

import com.bit.productservice.exceptions.productisnotfavorite.ProductIsNotFavoriteException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class FavoriteProductValidator {
    public void validateFavoriteProduct(ProductRepository productRepository, String productId, String userCode, FavoriteProductRepository favoriteProductRepository){
        if (productRepository.getProductById(productId) == null){
            throw new ProductNotFoundException("Product not found.");
        }

        if (!favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new ProductIsNotFavoriteException("Product is not favorite.");
        }
    }
}
