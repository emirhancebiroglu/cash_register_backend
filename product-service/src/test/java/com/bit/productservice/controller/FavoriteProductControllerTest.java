package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.service.FavoriteProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FavoriteProductControllerTest {
    @Mock
    private FavoriteProductService favoriteProductService;

    @InjectMocks
    private FavoriteProductController favoriteProductController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductToFavorites() {
        String productId = "123";

        ResponseEntity<String> response = favoriteProductController.addProductToFavorites(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product added to favorites successfully!", response.getBody());
        verify(favoriteProductService, times(1)).addProductToFavorites(productId);
    }

    @Test
    void testRemoveProductFromFavorites() {
        String productId = "123";

        ResponseEntity<String> response = favoriteProductController.removeProductFromFavorites(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product removed from favorites successfully!", response.getBody());
        verify(favoriteProductService, times(1)).removeProductFromFavorites(productId);
    }

    @Test
    void testListFavoriteProductsForCurrentUser() {
        int pageNo = 0;
        int pageSize = 15;
        String searchTerm = "search";
        String stockStatus = "available";
        String sortBy = "name";
        String sortOrder = "ASC";

        List<ProductDTO> productList = new ArrayList<>();
        productList.add(new ProductDTO());
        productList.add(new ProductDTO());

        when(favoriteProductService.listFavoriteProductsForCurrentUser(pageNo, pageSize, searchTerm, stockStatus, sortBy, sortOrder))
                .thenReturn(productList);

        favoriteProductController.listFavoriteProductsForCurrentUser(pageNo, pageSize, searchTerm, stockStatus, sortBy, sortOrder);

        verify(favoriteProductService, times(1)).listFavoriteProductsForCurrentUser(pageNo, pageSize, searchTerm, stockStatus, sortBy, sortOrder);
    }
}