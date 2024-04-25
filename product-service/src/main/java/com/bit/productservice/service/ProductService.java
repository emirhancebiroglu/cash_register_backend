package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
import com.bit.productservice.dto.SpecifyStockNumberReq;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface for product service.
 */
public interface ProductService {
    /**
     * Retrieves a list of products based on the specified parameters.
     *
     * @param pageNo          the page number to retrieve
     * @param pageSize        the number of products per page
     * @param searchTerm      the search term to filter products
     * @param lettersToFilter the letters to filter products by
     * @param existenceStatus the existence status of the products
     * @param stockStatus     the stock status of the products
     * @param sortBy          the field to sort by
     * @param sortOrder       the order to sort by (ascending or descending)
     * @return a list of product DTOs
     */
    List<ProductDTO> getProducts(Integer pageNo, Integer pageSize, String searchTerm, String lettersToFilter, String existenceStatus, String stockStatus, String sortBy, String sortOrder);

    /**
     * Adds a product.
     *
     * @param addProductReq the request body containing product information
     * @param file          the image file of the product
     * @throws IOException if an I/O error occurs
     */
    void addProduct(AddProductReq addProductReq, MultipartFile file) throws IOException;

    /**
     * Updates a product.
     *
     * @param productId         the ID of the product to update
     * @param updateProductReq  the request body containing updated product information
     * @param file              the image file of the product
     * @throws IOException if an I/O error occurs
     */
    void updateProduct(String productId, UpdateProductReq updateProductReq, MultipartFile file) throws IOException;

    /**
     * Deletes a product.
     *
     * @param productId the ID of the product to delete
     */
    void deleteProduct(String productId);

    /**
     * Re-adds a product with the specified stock number.
     *
     * @param productId             The ID of the product to be re-added.
     * @param specifyStockNumberReq An object containing the specified stock number for the product.
     */
    void reAddProduct(String productId, SpecifyStockNumberReq specifyStockNumberReq);

    /**
     * Checks product information by code.
     *
     * @param code the code of the product to check
     * @return a mono emitting the product info
     */
    Mono<ProductInfo> checkProduct(String code);

    /**
     * Updates stocks of products.
     *
     * @param productsIdWithQuantity a map containing product IDs and their corresponding quantities
     * @param shouldDecrease         a boolean indicating whether to decrease the stock
     */
    void updateStocks(Map<String, Integer> productsIdWithQuantity, boolean shouldDecrease);
}
