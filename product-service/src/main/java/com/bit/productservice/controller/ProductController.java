package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get-products-with-null-barcode")
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
}
