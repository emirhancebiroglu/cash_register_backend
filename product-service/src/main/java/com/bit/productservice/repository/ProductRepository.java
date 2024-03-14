package com.bit.productservice.repository;

import com.bit.productservice.entity.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    @NonNull Page<Product> findAll(@NonNull Specification spec, @NonNull Pageable pageable);
    Page<Product> findByBarcodeStartingWith(String barcode, Pageable pageable);
    Page<Product> findByProductCodeStartingWith(String productCode ,Pageable pageable);
    Product getProductById(String id);
}
