package com.bit.productservice.validators;

import com.bit.productservice.entity.Product;
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
    private final String userCode = "user123";

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
        verify(favoriteProductRepository, never()).existsByUserCodeAndProductId(userCode, productId);
    }

    @Test
    void validateFavoriteProduct_productDoesNotExistAndIsntFavorite_throwsProductIsNotFavoriteException() {
        when(favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)).thenReturn(false);

        assertThrows(ProductIsNotFavoriteException.class, () -> favoriteProductValidator.isProductNotFavorite(productId, userCode, favoriteProductRepository));

        verify(favoriteProductRepository, times(1)).existsByUserCodeAndProductId(userCode, productId);
    }
}