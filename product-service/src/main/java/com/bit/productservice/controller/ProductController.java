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

    @GetMapping("/get-products-with-pagination")
    public List<ProductDTO> getProductsByPagination(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "30") Integer pageSize) {
        return productService.getProductsByPagination(pageNo, pageSize);
    }

    @GetMapping("/get-products-with-sorting-and-pagination")
    public List<ProductDTO> getProductsBySortingAndPagination(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam String sortDirection) {
        return productService.getProductsBySortingAndPagination(pageNo, pageSize, sortDirection);
    }

    @GetMapping("/get-products-with-filter-and-pagination")
    public List<ProductDTO> getProductsByFilterAndPagination(
            @RequestParam String letter,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "30") Integer pageSize) {
        return productService.getProductsByFilterAndPagination(letter, pageNo, pageSize);
    }
}
