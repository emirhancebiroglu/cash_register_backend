package com.bit.productservice.repository;

import com.bit.productservice.entity.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing products.
 * Extends JpaRepository for CRUD operations on Product entities.
 * Extends JpaSpecificationExecutor for executing dynamic queries.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    /**
     * Retrieves all products that match the given specification.
     *
     * @param spec     the specification to apply
     * @param pageable pagination information
     * @return a page of products
     */
    @NonNull Page<Product> findAll(@NonNull Specification<Product> spec, @NonNull Pageable pageable);

    /**
     * Retrieves a page of products whose barcode starts with the given value.
     *
     * @param barcode  the starting value of the barcode
     * @param pageable pagination information
     * @return a page of products
     */
    Page<Product> findByBarcodeStartingWith(String barcode, Pageable pageable);

    /**
     * Retrieves a page of products whose product code starts with the given value.
     *
     * @param productCode the starting value of the product code
     * @param pageable    pagination information
     * @return a page of products
     */
    Page<Product> findByProductCodeStartingWith(String productCode ,Pageable pageable);

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product
     * @return the product entity
     */
    Product getProductById(String id);

    /**
     * Checks if a product with the given name exists.
     *
     * @param name the name to check
     * @return true if a product with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a product with the given product code exists.
     *
     * @param productCode the product code to check
     * @return true if a product with the product code exists, false otherwise
     */
    boolean existsByProductCode(String productCode);

    /**
     * Checks if a product with the given barcode exists.
     *
     * @param barcode the barcode to check
     * @return true if a product with the barcode exists, false otherwise
     */
    boolean existsByBarcode(String barcode);

    /**
     * Retrieves a product by its product code.
     *
     * @param code the product code
     * @return the product entity
     */
    Product findByProductCode(String code);

    /**
     * Retrieves a product by its barcode.
     *
     * @param code the barcode
     * @return the product entity
     */
    Product findByBarcode(String code);
}
