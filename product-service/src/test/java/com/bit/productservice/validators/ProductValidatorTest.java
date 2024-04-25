package com.bit.productservice.validators;

import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.exceptions.bothcodetypeprovided.BothCodeTypeProvidedException;
import com.bit.productservice.exceptions.nocodeprovided.NoCodeProvidedException;
import com.bit.productservice.exceptions.productwithsamebarcode.ProductWithSameBarcodeException;
import com.bit.productservice.exceptions.productwithsamename.ProductWithSameNameException;
import com.bit.productservice.exceptions.productwithsameproductcode.ProductWithSameProductCodeException;
import com.bit.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductValidatorTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductValidator productValidator;

    private UpdateProductReq updateProductReq;
    private AddProductReq addProductReq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productValidator = new ProductValidator(productRepository);
        updateProductReq = new UpdateProductReq(
                null,
                null,
                null,
                null,
                null,
                null
        );

        addProductReq = new AddProductReq();
        addProductReq.setName("Test Product");
        addProductReq.setPrice(10.0);
        addProductReq.setCategory("Test Category");
        addProductReq.setStockAmount(5);
        addProductReq.setProductCode("code");
    }

    @Test
    void validateAddProductReq_ValidRequest_NoErrors() {
        MultipartFile file = mock(MultipartFile.class);

        List<String> errors = productValidator.validateAddProductReq(addProductReq, file);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateAddProductReq_InvalidRequest_ReturnsErrors() {
        addProductReq.setName(null);
        addProductReq.setPrice(null);
        addProductReq.setCategory(null);
        addProductReq.setStockAmount(null);

        List<String> errors = productValidator.validateAddProductReq(addProductReq, null);

        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        assertTrue(errors.contains("Name field is required"));
        assertTrue(errors.contains("Price field is required"));
        assertTrue(errors.contains("Category field is required"));
        assertTrue(errors.contains("Stock field is required"));
        assertTrue(errors.contains("Image field is required"));
    }

    @Test
    void validateAddProductReq_PriceNegative_ReturnsError() {
        addProductReq.setPrice(-10.0);
        MultipartFile file = mock(MultipartFile.class);

        List<String> errors = productValidator.validateAddProductReq(addProductReq, file);

        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("Price field cannot be negative"));
    }

    @Test
    void validateAddProductReq_StockAmountNegative_ReturnsError() {
        addProductReq.setStockAmount(-5);
        MultipartFile file = mock(MultipartFile.class);

        List<String> errors = productValidator.validateAddProductReq(addProductReq, file);

        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("Stock field cannot be negative"));
    }

    @Test
    void validateProduct_BothCodeTypeProvided_ThrowsException() {
        addProductReq.setProductCode("123");
        addProductReq.setBarcode("456");

        assertThrows(BothCodeTypeProvidedException.class, () -> productValidator.validateProduct(addProductReq, updateProductReq));
    }

    @Test
    void validateProduct_NoCodeProvided_ThrowsException() {
        addProductReq.setProductCode(null);
        addProductReq.setBarcode(null);

        assertThrows(NoCodeProvidedException.class, () -> productValidator.validateAddProductReq(addProductReq, null));
    }

    @Test
    void validateProduct_ProductWithSameNameExists_ThrowsException() {
        addProductReq.setName("Existing Product");

        when(productRepository.existsByName(addProductReq.getName())).thenReturn(true);

        assertThrows(ProductWithSameNameException.class, () -> productValidator.validateProduct(addProductReq, updateProductReq));
    }

    @Test
    void validateProduct_ProductWithSameBarcodeExists_ThrowsException() {
        addProductReq.setProductCode(null);
        addProductReq.setBarcode("123");

        when(productRepository.existsByBarcode(addProductReq.getBarcode())).thenReturn(true);

        assertThrows(ProductWithSameBarcodeException.class, () -> productValidator.validateProduct(addProductReq, updateProductReq));
    }

    @Test
    void validateProduct_ProductWithSameProductCodeExists_ThrowsException() {
        addProductReq.setProductCode("123");

        when(productRepository.existsByProductCode(addProductReq.getProductCode())).thenReturn(true);

        assertThrows(ProductWithSameProductCodeException.class, () -> productValidator.validateProduct(addProductReq, updateProductReq));
    }
}