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

public interface ProductService {
    List<ProductDTO> getProducts();
    List<ProductDTO> getProductsByNullBarcodeWithFilter(String letter, Integer pageNo, Integer pageSize);
    List<ProductDTO> searchProductByProductCode(String productCode, Integer pageNo, Integer pageSize);
    List<ProductDTO> searchProductByBarcode(String barcode, Integer pageNo, Integer pageSize);
    void addProduct(AddProductReq addProductReq, MultipartFile file) throws IOException;
    void updateProduct(String productId, UpdateProductReq updateProductReq, MultipartFile file) throws IOException;
    void deleteProduct(String productId);
    void reAddProduct(String productId);
    Mono<ProductInfo> checkProduct(String code);
    void updateStocks(Map<String, Integer> productsIdWithQuantity, boolean shouldDecrease);
}
