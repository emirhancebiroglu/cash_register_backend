package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.entity.FavoriteProduct;
import com.bit.productservice.entity.Image;
import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.productisnotfavorite.ProductIsNotFavoriteException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.serviceimpl.FavoriteProductServiceImpl;
import com.bit.productservice.validators.FavoriteProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FavoriteProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private FavoriteProductRepository favoriteProductRepository;
    @Mock
    private FavoriteProductValidator favoriteProductValidator;

    @InjectMocks
    private FavoriteProductServiceImpl favoriteProductService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user123", null));
    }

    @Test
    void addProductToFavorites_ValidProduct_Success() {
        String productId = "123";
        when(productRepository.existsById(productId)).thenReturn(true);

        favoriteProductService.addProductToFavorites(productId);

        verify(favoriteProductRepository, times(1)).save(any(FavoriteProduct.class));
    }

    @Test
    void addProductToFavorites_InvalidProduct_ThrowsException() {
        String productId = "456";
        when(productRepository.existsById(productId)).thenReturn(false);

        doThrow(IllegalArgumentException.class)
                .when(favoriteProductValidator)
                .validateFavoriteProduct(any(), any(), any(), any());

        assertThrows(IllegalArgumentException.class, () -> favoriteProductService.addProductToFavorites(productId));

        verify(favoriteProductRepository, never()).save(any(FavoriteProduct.class));
    }

    @Test
    void removeProductFromFavorites_ValidProduct_Success() {
        String productId = "123";
        when(favoriteProductRepository.existsByUserCodeAndProductId("user123", productId)).thenReturn(true);

        favoriteProductService.removeProductFromFavorites(productId);

        verify(favoriteProductRepository, times(1)).deleteByUserCodeAndProductId("user123", productId);
    }

    @Test
    void removeProductFromFavorites_ProductIstNotFavorite_ThrowsException() {
        String productId = "456";
        String userCode = "user123";

        doThrow(ProductIsNotFavoriteException.class)
                .when(favoriteProductValidator)
                .validateFavoriteProduct(productRepository, productId, userCode, favoriteProductRepository);

        assertThrows(ProductIsNotFavoriteException.class, () -> favoriteProductService.removeProductFromFavorites(productId));

        verify(favoriteProductRepository, never()).deleteByUserCodeAndProductId(anyString(), anyString());
    }

    @Test
    void removeProductFromFavorites_ProductIstNotFound_ThrowsException() {
        String productId = "456";
        String userCode = "user123";

        doThrow(ProductNotFoundException.class)
                .when(favoriteProductValidator)
                .validateFavoriteProduct(productRepository, productId, userCode, favoriteProductRepository);

        when(productRepository.getProductById(productId)).thenThrow(ProductNotFoundException.class);

        assertThrows(ProductNotFoundException.class, () -> favoriteProductService.removeProductFromFavorites(productId));

        verify(favoriteProductRepository, never()).deleteByUserCodeAndProductId(anyString(), anyString());
    }

    @Test
    void listFavoriteProductsForCurrentUser_Success() {
        String userCode = "user123";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<FavoriteProduct> favoriteProductPage = mock(Page.class);
        when(favoriteProductRepository.findByUserCode(userCode, pageRequest)).thenReturn(favoriteProductPage);

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setProductId("456");
        List<FavoriteProduct> favoriteProductsList = Collections.singletonList(favoriteProduct);
        when(favoriteProductPage.getContent()).thenReturn(favoriteProductsList);

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(10.0);
        product.setCategory("Test Category");
        product.setImage(new Image());
        when(productRepository.getProductById("456")).thenReturn(product);

        List<ProductDTO> result = favoriteProductService.listFavoriteProductsForCurrentUser(0, 10);

        assertEquals(1, result.size());
        ProductDTO productDTO = result.get(0);
        assertEquals("Test Product", productDTO.getName());
        assertEquals(10.0, productDTO.getPrice());
        assertEquals("Test Category", productDTO.getCategory());
    }
}
