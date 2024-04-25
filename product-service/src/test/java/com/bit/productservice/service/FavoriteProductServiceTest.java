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
import com.bit.productservice.utils.JwtUtil;
import com.bit.productservice.utils.SortApplier;
import com.bit.productservice.validators.FavoriteProductValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Mock
    private SortApplier sortApplier;

    @Mock
    @Getter
    private HttpServletRequest request;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private FavoriteProductServiceImpl favoriteProductService;

    private String token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        token = "valid_jwt_token";
    }

    @Test
    void addProductToFavorites_ValidProduct_Success() {
        String productId = "123";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        favoriteProductService.addProductToFavorites(productId);

        verify(favoriteProductRepository, times(1)).save(any(FavoriteProduct.class));
    }

    @Test
    void addProductToFavorites_InvalidProduct_ThrowsException() {
        String productId = "456";
        when(productRepository.existsById(productId)).thenReturn(false);

        doThrow(ProductNotFoundException.class)
                .when(favoriteProductValidator)
                .isProductExist(any(), any());

        assertThrows(ProductNotFoundException.class, () -> favoriteProductService.addProductToFavorites(productId));

        verify(favoriteProductRepository, never()).save(any(FavoriteProduct.class));
    }

    @Test
    void removeProductFromFavorites_ValidProduct_Success() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);
        String productId = "123";

        when(favoriteProductRepository.existsByUserIdAndProductId(1L, productId)).thenReturn(true);

        favoriteProductService.removeProductFromFavorites(productId);

        verify(favoriteProductRepository, times(1)).deleteByUserIdAndProductId(1L, productId);
    }

    @Test
    void removeProductFromFavorites_ProductIstNotFavorite_ThrowsException() {
        String productId = "456";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);


        doThrow(ProductIsNotFavoriteException.class)
                .when(favoriteProductValidator)
                .isProductNotFavorite(productId, 1L, favoriteProductRepository);

        assertThrows(ProductIsNotFavoriteException.class, () -> favoriteProductService.removeProductFromFavorites(productId));

        verify(favoriteProductRepository, never()).deleteByUserIdAndProductId(anyLong(), anyString());
    }

    @Test
    void removeProductFromFavorites_ProductIstNotFound_ThrowsException() {
        String productId = "456";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(1L);


        doThrow(ProductNotFoundException.class)
                .when(favoriteProductValidator)
                .isProductExist(productRepository, productId);

        when(productRepository.getProductById(productId)).thenThrow(ProductNotFoundException.class);

        assertThrows(ProductNotFoundException.class, () -> favoriteProductService.removeProductFromFavorites(productId));

        verify(favoriteProductRepository, never()).deleteByUserIdAndProductId(anyLong(), anyString());
    }

    @Test
    void listFavoriteProductsForCurrentUser_Success() {
        Image image = new Image();
        image.setImageUrl("example.com");

        Product product = new Product();
        product.setName("Apple");
        product.setImage(image);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        when(sortApplier.applySortForFavoriteProducts(0, 10, "name", "asc")).thenReturn(pageable);

        List<FavoriteProduct> favoriteProducts = new ArrayList<>();
        favoriteProducts.add(new FavoriteProduct(1L, product));

        Page<FavoriteProduct> favoriteProductPage = new PageImpl<>(favoriteProducts, pageable, favoriteProducts.size());
        when(favoriteProductRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(favoriteProductPage);

        when(productRepository.findAllById(anyList())).thenReturn(favoriteProducts.stream().map(FavoriteProduct::getProduct).collect(Collectors.toList()));

        List<ProductDTO> products = favoriteProductService.listFavoriteProductsForCurrentUser(0, 10, null, null, "name", "asc");

        assertEquals(favoriteProducts.size(), products.size());
    }

}
