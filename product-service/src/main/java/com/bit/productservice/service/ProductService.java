package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getProducts();
    List<ProductDTO> getProductsByNullBarcodeWithFilter(String letter, Integer pageNo, Integer pageSize);
    List<ProductDTO> searchProductByProductCode(String productCode, Integer pageNo, Integer pageSize);
    List<ProductDTO> searchProductByBarcode(String barcode, Integer pageNo, Integer pageSize);
}
