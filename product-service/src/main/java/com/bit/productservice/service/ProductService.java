package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
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
     * Retrieves all products.
     *
     * @return a list of product DTOs
     */
    List<ProductDTO> getProducts();

    /**
     * Retrieves products with null barcode and filters by letter.
     *
     * @param letter    the starting letter of the product name
     * @param pageNo    the page number
     * @param pageSize  the size of each page
     * @return a list of product DTOs
     */
    List<ProductDTO> getProductsByNullBarcodeWithFilter(String letter, Integer pageNo, Integer pageSize);

    /**
     * Searches for products by code.
     *
     * @param searchType The search type (barcode or productCode).
     * @param searchTerm the term includes code to search a specific product.
     * @param pageNo      the page number.
     * @param pageSize    the size of each page.
     * @return a list of product DTOs.
     */
    List<ProductDTO> searchProductByCode(String searchType, String searchTerm, Integer pageNo, Integer pageSize);

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
     * Re-adds a product.
     *
     * @param productId the ID of the product to re-add
     */
    void reAddProduct(String productId);

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
