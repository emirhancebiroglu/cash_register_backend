package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
import com.bit.productservice.dto.SpecifyStockNumberReq;
import com.bit.productservice.dto.UpdateStockRequest;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        SpecifyStockNumberReq specifyStockNumberReq = new SpecifyStockNumberReq();
        specifyStockNumberReq.setStockNumber(3);

        ResponseEntity<String> response = productController.reAddProduct(productId, specifyStockNumberReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product re-added successfully", response.getBody());
        verify(productService, times(1)).reAddProduct(productId, specifyStockNumberReq);
    }

    @Test
    void checkProduct() {
        ProductInfo productInfo = new ProductInfo(true, "Product Name", 10.0, 100);
        when(productService.checkProduct(anyString())).thenReturn(Mono.just(productInfo));

        Mono<ProductInfo> response = productController.checkProduct("productCode");

        StepVerifier.create(response)
                .expectNextMatches(info -> info.isExists() && info.getName().equals("Product Name") && info.getPrice() == 10.0 && info.getStockAmount() == 100)
                .verifyComplete();

        verify(productService, times(1)).checkProduct("productCode");
    }

    @Test
    void updateStocks() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setProductsIdWithQuantity(Map.of("productId1", 10, "productId2", 20));
        request.setShouldDecrease(true);

        productController.updateStocks(request);

        verify(productService, times(1)).updateStocks(request.getProductsIdWithQuantity(), request.isShouldDecrease());
    }

    @Test
    void testGetProducts() {
        int pageNo = 0;
        int pageSize = 15;
        String searchTerm = "search";
        String lettersToFilter = "letters";
        String existenceStatus = "existing";
        String stockStatus = "available";
        String sortBy = "name";
        String sortOrder = "ASC";

        List<ProductDTO> productList = new ArrayList<>();
        productList.add(new ProductDTO());
        productList.add(new ProductDTO());

        when(productService.getProducts(pageNo, pageSize, searchTerm, lettersToFilter, existenceStatus, stockStatus, sortBy, sortOrder))
                .thenReturn(productList);

        ResponseEntity<List<ProductDTO>> responseEntity = productController.getProducts(pageNo, pageSize, searchTerm, lettersToFilter, existenceStatus, stockStatus, sortBy, sortOrder);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productList, responseEntity.getBody());
        verify(productService, times(1)).getProducts(pageNo, pageSize, searchTerm, lettersToFilter, existenceStatus, stockStatus, sortBy, sortOrder);
    }
}