package com.bit.productservice.repository;

import com.bit.productservice.entity.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @NonNull List<Product> findAll(@NonNull Sort sortByNameAsc);
    Page<Product> findByBarcodeStartingWith(String barcode, Pageable pageable);
    Page<Product> findByProductCodeStartingWith(String productCode ,Pageable pageable);
    Page<Product> findByBarcodeIsNull(Specification<Product> specification, Pageable pageable);
    Product getProductById(Long id);
}
