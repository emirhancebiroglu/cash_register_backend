package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/get-products")
    public List<ProductDTO> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/search-products-by-product-code")
    public List<ProductDTO> searchProductByProductCode(@RequestParam String productCode,
                                                       @RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "15") Integer pageSize) {
        return productService.searchProductByProductCode(productCode, pageNo, pageSize);
    }

    @GetMapping("/get-products-with-filter-and-pagination")
    public List<ProductDTO> getProductsByFilterAndPagination(
            @RequestParam String letter,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        return productService.getProductsByNullBarcodeWithFilter(letter, pageNo, pageSize);
    }

    @GetMapping("/search-products-by-barcode")
    public List<ProductDTO> searchProductByBarcode(
            @RequestParam String barcode,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        return productService.searchProductByBarcode(barcode, pageNo, pageSize);
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> addProduct(@RequestParam(value = "image", required = false) MultipartFile file, AddProductReq addProductReq) throws IOException {
        productService.addProduct(addProductReq, file);
        return ResponseEntity.status(HttpStatus.OK).body("Product added successfully");
    }

    @PutMapping("/update-product/{productId}")
    public ResponseEntity<String> updateProduct(@RequestParam(value = "image", required = false) MultipartFile file,
                                                @PathVariable String productId,
                                                UpdateProductReq updateProductReq) throws IOException {
        productService.updateProduct(productId, updateProductReq, file);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated successfully");

    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable String productId){
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");

    }

    @PostMapping("/re-add-product/{productId}")
    public ResponseEntity<String> reAddProduct(@PathVariable String productId){
        productService.reAddProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body("Product re-added successfully");

    }
}
