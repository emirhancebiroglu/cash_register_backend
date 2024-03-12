package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getProducts();
    List<ProductDTO> getProductsByNullBarcodeWithFilter(String letter, Integer pageNo, Integer pageSize);
    List<ProductDTO> searchProductByProductCode(String productCode, Integer pageNo, Integer pageSize);
    List<ProductDTO> searchProductByBarcode(String barcode, Integer pageNo, Integer pageSize);
    void addProduct(AddProductReq addProductReq);
    void updateProduct(Long productId, UpdateProductReq updateProductReq);
    void deleteProduct(Long productId);
}
