package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductControllerTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts() {
        List<ProductDTO> productList = new ArrayList<>();
        when(productService.getProducts()).thenReturn(productList);

        List<ProductDTO> response = productController.getProducts();

        assertEquals(productList, response);
        verify(productService, times(1)).getProducts();
    }

    @Test
    void searchProductByProductCode() {
        List<ProductDTO> productList = new ArrayList<>();
        when(productService.searchProductByProductCode(anyString(), anyInt(), anyInt())).thenReturn(productList);

        List<ProductDTO> response = productController.searchProductByProductCode("123", 0, 15);

        assertEquals(productList, response);
        verify(productService, times(1)).searchProductByProductCode("123", 0, 15);
    }

    @Test
    void searchProductByBarcode() {
        List<ProductDTO> productList = new ArrayList<>();
        when(productService.searchProductByBarcode(anyString(), anyInt(), anyInt())).thenReturn(productList);

        List<ProductDTO> response = productController.searchProductByBarcode("123", 0, 15);

        assertEquals(productList, response);
        verify(productService, times(1)).searchProductByBarcode("123", 0, 15);
    }

    @Test
    void getProductsByFilterAndPagination() {
        List<ProductDTO> productList = new ArrayList<>();
        when(productService.getProductsByNullBarcodeWithFilter(anyString(), anyInt(), anyInt())).thenReturn(productList);

        List<ProductDTO> response = productController.getProductsByFilterAndPagination("A", 0, 15);

        assertEquals(productList, response);
        verify(productService, times(1)).getProductsByNullBarcodeWithFilter("A", 0, 15);
    }

    @Test
    void addProduct() throws IOException {
        AddProductReq addProductReq = new AddProductReq();

        ResponseEntity<String> response = productController.addProduct(null, addProductReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product added successfully", response.getBody());
        verify(productService, times(1)).addProduct(addProductReq, null);
    }

    @Test
    void updateProduct() throws IOException {
        String productId = "123";
        UpdateProductReq updateProductReq = new UpdateProductReq(
                "123",
                null,
                "test",
                null,
                null,
                null
        );

        ResponseEntity<String> response = productController.updateProduct(null, productId, updateProductReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully", response.getBody());
        verify(productService, times(1)).updateProduct(productId, updateProductReq, null);
    }

    @Test
    void deleteProduct() {
        String productId = "123";

        ResponseEntity<String> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    void reAddProduct() {
        String productId = "123";

        ResponseEntity<String> response = productController.reAddProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product re-added successfully", response.getBody());
        verify(productService, times(1)).reAddProduct(productId);
    }
}