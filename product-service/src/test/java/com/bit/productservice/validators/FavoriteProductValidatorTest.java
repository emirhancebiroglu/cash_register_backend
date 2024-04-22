package com.bit.productservice.validators;

import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.productalreadyinfavorite.ProductAlreadyInFavoriteException;
import com.bit.productservice.exceptions.productisnotfavorite.ProductIsNotFavoriteException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteProductValidatorTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private FavoriteProductRepository favoriteProductRepository;

    @InjectMocks
    private FavoriteProductValidator favoriteProductValidator;

    private final String productId = "123456789";
    private final Long userId = 1L;

    @Test
    void validateFavoriteProduct_productExists_productIsFavorite() {
        when(productRepository.getProductById(productId)).thenReturn(new Product());
        favoriteProductValidator.isProductExist(productRepository, productId);

        verify(productRepository, times(1)).getProductById(productId);
    }

    @Test
    void validateFavoriteProduct_productDoesNotExist_throwsProductNotFoundException() {
        when(productRepository.getProductById(productId)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> favoriteProductValidator.isProductExist(productRepository, productId));

        verify(productRepository, times(1)).getProductById(productId);
        verify(favoriteProductRepository, never()).existsByUserIdAndProductId(userId, productId);
    }

    @Test
    void validateFavoriteProduct_productDoesNotExistAndIsntFavorite_throwsProductIsNotFavoriteException() {
        when(favoriteProductRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);

        assertThrows(ProductIsNotFavoriteException.class, () -> favoriteProductValidator.isProductNotFavorite(productId, userId, favoriteProductRepository));

        verify(favoriteProductRepository, times(1)).existsByUserIdAndProductId(userId, productId);
    }

    @Test
    void validateFavoriteProduct_productIsAlreadyFavorite_throwsProductAlreadyInFavoriteException() {
        when(favoriteProductRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

        assertThrows(ProductAlreadyInFavoriteException.class,
                () -> favoriteProductValidator.isProductFavorite(productId, userId, favoriteProductRepository));

        verify(favoriteProductRepository, times(1)).existsByUserIdAndProductId(userId, productId);
    }
}