package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
import com.bit.productservice.dto.UpdateStockRequest;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

/**
 * Controller class responsible for handling HTTP requests related to products.
 */
@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * Endpoint to retrieve all products.
     *
     * @return List of ProductDTO objects representing products.
     */
    @GetMapping("/get-products")
    public List<ProductDTO> getProducts() {
        return productService.getProducts();
    }

    /**
     * Endpoint to search for products by product code.
     *
     * @param productCode The code of the product to search for.
     * @param pageNo      The page number for pagination (default: 0).
     * @param pageSize    The size of each page for pagination (default: 15).
     * @return List of ProductDTO objects representing matching products.
     */
    @GetMapping("/search-products-by-product-code")
    public List<ProductDTO> searchProductByProductCode(@RequestParam String productCode,
                                                       @RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "15") Integer pageSize) {
        return productService.searchProductByProductCode(productCode, pageNo, pageSize);
    }

    /**
     * Endpoint to retrieve products with filtering and pagination by a specific letter.
     *
     * @param letter   The starting letter for filtering products.
     * @param pageNo   The page number for pagination (default: 0).
     * @param pageSize The size of each page for pagination (default: 15).
     * @return List of ProductDTO objects representing filtered products.
     */
    @GetMapping("/get-products-with-filter-and-pagination")
    public List<ProductDTO> getProductsByFilterAndPagination(
            @RequestParam String letter,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        return productService.getProductsByNullBarcodeWithFilter(letter, pageNo, pageSize);
    }

    /**
     * Endpoint to search for products by barcode.
     *
     * @param barcode  The barcode of the product to search for.
     * @param pageNo   The page number for pagination (default: 0).
     * @param pageSize The size of each page for pagination (default: 15).
     * @return List of ProductDTO objects representing matching products.
     */
    @GetMapping("/search-products-by-barcode")
    public List<ProductDTO> searchProductByBarcode(
            @RequestParam String barcode,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        return productService.searchProductByBarcode(barcode, pageNo, pageSize);
    }

    /**
     * Endpoint to check product information by its code.
     *
     * @param code The code of the product to check.
     * @return Mono emitting ProductInfo object representing product information.
     */
    @GetMapping("/check-product")
    public Mono<ProductInfo> checkProduct(@RequestParam String code){
        return productService.checkProduct(code);
    }

    /**
     * Endpoint to add a new product.
     *
     * @param file         Optional image file for the product.
     * @param addProductReq The request object containing information about the new product.
     * @return ResponseEntity indicating the status of the operation.
     * @throws IOException If an I/O error occurs while processing the file.
     */
    @PostMapping("/add-product")
    public ResponseEntity<String> addProduct(@RequestParam(value = "image", required = false) MultipartFile file, AddProductReq addProductReq) throws IOException {
        productService.addProduct(addProductReq, file);
        return ResponseEntity.status(HttpStatus.OK).body("Product added successfully");
    }

    /**
     * Endpoint to update an existing product.
     *
     * @param file            Optional image file for the updated product.
     * @param productId       The ID of the product to update.
     * @param updateProductReq The request object containing updated information about the product.
     * @return ResponseEntity indicating the status of the operation.
     * @throws IOException If an I/O error occurs while processing the file.
     */
    @PutMapping("/update-product/{productId}")
    public ResponseEntity<String> updateProduct(@RequestParam(value = "image", required = false) MultipartFile file,
                                                @PathVariable String productId,
                                                UpdateProductReq updateProductReq) throws IOException {
        productService.updateProduct(productId, updateProductReq, file);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated successfully");

    }
    /**
     * Endpoint to delete a product.
     *
     * @param productId The ID of the product to delete.
     * @return ResponseEntity indicating the status of the operation.
     */
    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId){
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");

    }

    /**
     * Endpoint to re-add a deleted product.
     *
     * @param productId The ID of the product to re-add.
     * @return ResponseEntity indicating the status of the operation.
     */
    @PostMapping("/re-add-product/{productId}")
    public ResponseEntity<String> reAddProduct(@PathVariable String productId){
        productService.reAddProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product re-added successfully");

    }

    /**
     * Endpoint to update stocks of products.
     *
     * @param request The request object containing product IDs and quantities.
     */
    @PostMapping("/update-stocks")
    public void updateStocks(@RequestBody UpdateStockRequest request){
        productService.updateStocks(request.getProductsIdWithQuantity(), request.isShouldDecrease());
    }
}
