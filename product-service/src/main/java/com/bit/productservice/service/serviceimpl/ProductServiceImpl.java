package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.entity.Product;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getProducts() {
        Sort sortByNameAsc = Sort.by("name").ascending();
        List<Product> products = productRepository.findAll(sortByNameAsc);
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> searchProductByProductCode(String productCode ,Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("name").ascending());
        Page<Product> pagingProduct = productRepository.findByProductCodeStartingWith(productCode, pageRequest);
        return pagingProduct.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> searchProductByBarcode(String barcode, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("name").ascending());
        Page<Product> pagingProduct = productRepository.findByBarcodeStartingWith(barcode, pageRequest);
        return pagingProduct.getContent().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> getProductsByNullBarcodeWithFilter(String letter, Integer pageNo, Integer pageSize) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            switch (letter) {
                case "A" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "a%");
                }
                case "B" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "b%");
                }
                case "C-D" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "c", "d"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "C", "D")
                    );
                }
                case "E-F" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "e", "f"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "E", "F")
                    );
                }
                case "G-I" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "g", "i"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "G", "I")
                    );
                }
                case "K" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "k%");
                }
                case "L-N" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "l", "n"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "L", "N")
                    );
                }
                case "P" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "p%");
                }
                case "R-S" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "r", "s"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "R", "s")
                    );
                }
                case "Ş-T" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "ş", "t"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "Ş", "T")
                    );
                }
                case "Ü-Z" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "ü", "z"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "Ü", "Z")
                    );
                }
                default -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), letter + "%");
                }
            }
        };

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("name").ascending());
        Page<Product> pagingProduct = productRepository.findByBarcodeIsNull(specification, pageRequest);
        return pagingProduct.getContent().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void addProduct(AddProductReq addProductReq) {

    }

    @Override
    public void updateProduct(Long productId, UpdateProductReq updateProductReq) {

    }

    @Override
    public void deleteProduct(Long productId) {

    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getBarcode(),
                product.getProductCode(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                product.getCategory()
        );
    }
}