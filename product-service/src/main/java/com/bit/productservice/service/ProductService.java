package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getProductsByPagination(int pageNo, int pageSize);
    List<ProductDTO> getProducts();
    List<ProductDTO> getProductsBySortingAndPagination(int pageNo, int pageSize, String sortDirection);
    List<ProductDTO> getProductsByFilterAndPagination(String letter, Integer pageNo, Integer pageSize);
}
