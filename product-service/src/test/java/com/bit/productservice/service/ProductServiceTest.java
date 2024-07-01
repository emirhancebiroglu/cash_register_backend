package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
import com.bit.productservice.dto.SpecifyStockNumberReq;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.entity.Image;
import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.negativefield.NegativeFieldException;
import com.bit.productservice.exceptions.nulloremptyfield.NullOrEmptyFieldException;
import com.bit.productservice.exceptions.productalreadydeleted.ProductAlreadyDeletedException;
import com.bit.productservice.exceptions.productalreadyinstocks.ProductAlreadyInStocksException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.ImageRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.serviceimpl.ProductServiceImpl;
import com.bit.productservice.utils.SortApplier;
import com.bit.productservice.validators.ProductValidator;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductValidator productValidator;
    @Getter
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private SortApplier sortApplier;
    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private AddProductReq addProductReq;
    private UpdateProductReq updateProductReq;
    private String productId;
    private SpecifyStockNumberReq specifyStockNumberReq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productId = "testProductId";

        Image image = new Image();
        image.setImageUrl("example.com");

        product = new Product();
        product.setId(productId);
        product.setName("Apple");
        product.setDeleted(false);
        product.setImage(image);

        addProductReq = new AddProductReq();
        addProductReq.setName("Apple");
        addProductReq.setPrice(100.00);
        addProductReq.setStockAmount(100);
        addProductReq.setCategory("Fruits");

        updateProductReq = new UpdateProductReq();
        updateProductReq.setBarcode("123");
        updateProductReq.setName("Updated Apple");
        updateProductReq.setPrice(150.00);
        updateProductReq.setStockAmount(150);
        updateProductReq.setCategory("Updated Fruits");

        specifyStockNumberReq = new SpecifyStockNumberReq();
        specifyStockNumberReq.setStockNumber(50);
    }

    @Test
    void addProduct_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);

        when(productValidator.validateAddProductReq(addProductReq, file)).thenReturn(Collections.emptyList());

        productService.addProduct(addProductReq, file);

        verify(productValidator).validateProduct(addProductReq, null);
        verify(productRepository).save(any());
    }

    @Test
    void addProduct_NegativeFieldErrors() {
        addProductReq.setPrice(-100.00);

        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);

        List<String> errors = Collections.singletonList("Price cannot be negative");
        when(productValidator.validateAddProductReq(addProductReq, file)).thenReturn(errors);

        assertThrows(NegativeFieldException.class, () -> productService.addProduct(addProductReq, file));

        verifyNoInteractions(productRepository);
    }

    @Test
    void addProduct_NullOrEmptyFieldErrors(){
        addProductReq.setName("");

        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[0]);

        List<String> errors = Collections.singletonList("Name cannot be empty");
        when(productValidator.validateAddProductReq(addProductReq, file)).thenReturn(errors);

        assertThrows(NullOrEmptyFieldException.class, () -> productService.addProduct(addProductReq, file));

        verifyNoInteractions(productRepository);
    }

    @Test
    void updateProduct_Success_WithoutImage() throws IOException {
        Product dummyProduct = new Product();
        dummyProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        doNothing().when(productValidator).validateProduct(null, updateProductReq);

        productService.updateProduct(productId, updateProductReq, null);

        verify(productValidator).validateProduct(null, updateProductReq);
        verify(productRepository).save(dummyProduct);

        assertEquals(updateProductReq.getName(), dummyProduct.getName());
        assertEquals(updateProductReq.getPrice(), dummyProduct.getPrice());
        assertEquals(updateProductReq.getStockAmount(), dummyProduct.getStockAmount());
        assertEquals(updateProductReq.getCategory(), dummyProduct.getCategory());
        assertNotNull(dummyProduct.getLastUpdateDate());
    }

    @Test
    void updateProduct_Fails_NegativePriceField(){
        updateProductReq.setPrice(-150.00);

        Product dummyProduct = new Product();
        dummyProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));

        assertThrows(NegativeFieldException.class, () -> productService.updateProduct(productId, updateProductReq, null));
    }

    @Test
    void updateProduct_Fails_NegativeStockAmountField(){
        updateProductReq.setStockAmount(-150);

        Product dummyProduct = new Product();
        dummyProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));

        assertThrows(NegativeFieldException.class, () -> productService.updateProduct(productId, updateProductReq, null));
    }

    @Test
    void updateProduct_Success_WithImage() throws IOException {
        Resource resource = new ClassPathResource("static/images/apple.jpg");
        byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());

        MultipartFile file = new MockMultipartFile("file", "apple.jpg", "image/jpeg", fileContent);

        Product dummyProduct = new Product();
        dummyProduct.setId(productId);
        Image image = new Image();
        image.setImageUrl("oldImageUrl");
        image.setImageId("ja5vucwyg703qdjhbyna");
        dummyProduct.setImage(image);

        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        doNothing().when(productValidator).validateProduct(null, updateProductReq);

        CloudinaryService cloudinaryService = mock(CloudinaryService.class);
        when(cloudinaryService.upload(any(MultipartFile.class)))
                .thenReturn(Map.of("original_filename", "newImage.jpg", "url", "newImageUrl", "public_id", "newImageId"));
        doNothing().when(cloudinaryService).delete("ja5vucwyg703qdjhbyna");

        productService.updateProduct(productId, updateProductReq, file);

        verify(productValidator).validateProduct(null, updateProductReq);
        verify(productRepository, times(2)).save(dummyProduct);
        verify(imageRepository).save(any(Image.class));

        assertEquals(updateProductReq.getName(), dummyProduct.getName());
        assertEquals(updateProductReq.getPrice(), dummyProduct.getPrice());
        assertEquals(updateProductReq.getStockAmount(), dummyProduct.getStockAmount());
        assertEquals(updateProductReq.getCategory(), dummyProduct.getCategory());
        assertNotNull(dummyProduct.getLastUpdateDate());
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(productRepository).findById(product.getId());
        verify(productRepository).save(product);

        assertTrue(product.isDeleted());
        assertEquals(LocalDate.now(), product.getLastUpdateDate());
    }

    @Test
    void deleteProduct_ProductNotFound() {
        when(productRepository.getProductById(productId)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void testDeleteProduct_ProductAlreadyDeleted() {
        product.setDeleted(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductAlreadyDeletedException exception = assertThrows(ProductAlreadyDeletedException.class, () -> productService.deleteProduct(productId));

        assertEquals("Product already deleted", exception.getMessage());

        verify(productRepository, never()).save(any());
    }

    @Test
    void testCheckProduct() {
        String code = "ABC123";
        product.setBarcode("ABC123");
        product.setPrice(10.0);
        product.setStockAmount(5);

        when(productRepository.findByBarcode(code)).thenReturn(product);

        Mono<ProductInfo> productInfoMono = productService.checkProduct(code);

        StepVerifier.create(productInfoMono)
                .expectNextMatches(productInfo ->
                        productInfo.isExists() &&
                                productInfo.getName().equals("Apple") &&
                                productInfo.getPrice() == 10.0 &&
                                productInfo.getStockAmount() == 5)
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateStocks() {
        Map<String, Integer> productsIdWithQuantity = new HashMap<>();
        productsIdWithQuantity.put("ABC123", 5);
        productsIdWithQuantity.put("DEF456", 10);

        product.setProductCode("ABC123");
        product.setStockAmount(10);

        Product product2 = new Product();
        product2.setBarcode("DEF456");
        product2.setStockAmount(15);

        when(productRepository.findByProductCode("ABC123")).thenReturn(product);
        when(productRepository.findByBarcode("DEF456")).thenReturn(product2);

        productService.updateStocks(productsIdWithQuantity, true);

        verify(productRepository, times(1)).save(product);
        verify(productRepository, times(1)).save(product2);
    }

    @Test
    void reAddProduct_Success() {
        product.setDeleted(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.reAddProduct(product.getId(), specifyStockNumberReq));

        assertFalse(product.isDeleted());
        assertEquals(LocalDate.now(), product.getLastUpdateDate());
        assertEquals(50, product.getStockAmount());
    }

    @Test
    void reAddProduct_NegativeStockNumber() {
        specifyStockNumberReq.setStockNumber(-50);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(NegativeFieldException.class, this::getReAddProduct);
    }

    @Test
    void reAddProduct_ProductAlreadyInStocks() {
        product.setDeleted(false);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ProductAlreadyInStocksException.class, this::getReAddProduct);
    }

    @Test
    void getProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        List<Product> pageContent = Collections.singletonList(product);
        PageImpl<Product> page = new PageImpl<>(pageContent, pageable, pageContent.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        when(sortApplier.applySortForProducts(0, 10, "name", "asc")).thenReturn(pageable);

        List<ProductDTO> products = productService.getProducts(0, 10, "a", null, null, null, "name", "asc");

        assertEquals(pageContent.size(), products.size());
    }

    private void getReAddProduct() {
        productService.reAddProduct(product.getId(), specifyStockNumberReq);
    }
}
