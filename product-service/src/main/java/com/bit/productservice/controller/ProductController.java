package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
import com.bit.productservice.dto.SpecifyStockNumberReq;
import com.bit.productservice.dto.UpdateStockRequest;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.service.ExcelReportService;
import com.bit.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
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
    private final ExcelReportService excelReportService;

    /**
     * Endpoint to retrieve all products.
     *
     * @return List of ProductDTO objects representing products.
     */
    @GetMapping("/get-products")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0", required = false) Integer pageNo,
            @RequestParam(defaultValue = "15", required = false) Integer pageSize,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String lettersToFilter,
            @RequestParam(required = false) String existenceStatus,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(name = "sortBy", required = false, defaultValue = "name") String sortBy,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "ASC") String sortOrder
    ) {
        List<ProductDTO> products = productService.getProducts(pageNo, pageSize, searchTerm, lettersToFilter, existenceStatus, stockStatus, sortBy, sortOrder);
        return ResponseEntity.status(HttpStatus.OK).body(products);
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
    public ResponseEntity<String> reAddProduct(@PathVariable String productId, @RequestBody SpecifyStockNumberReq specifyStockNumberReq){
        productService.reAddProduct(productId, specifyStockNumberReq);
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

     /**
     * Endpoint to export product data to an Excel file.
     *
     * @return ResponseEntity containing the exported Excel file as an InputStreamResource.
     *         The response has appropriate headers for file download and content type.
     */
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportProductDataToExcel(){
        // Generate the Excel file from product data using the ExcelReportService
        ByteArrayInputStream in = excelReportService.exportProductDataToExcel();

        // Prepare HTTP headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        // Return the response with appropriate headers, content type, and the generated Excel file
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}