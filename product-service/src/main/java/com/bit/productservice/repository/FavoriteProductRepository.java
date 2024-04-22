    package com.bit.productservice.repository;

    import com.bit.productservice.entity.FavoriteProduct;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    /**
     * Repository interface for managing favorite products.
     * Extends JpaRepository for CRUD operations on FavoriteProduct entities.
     */
    @Repository
    public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, String> {
        /**
         * Finds favorite products by user code.
         *
         * @param userCode The user code to search for.
         * @param pageable Pagination information.
         * @return A page of favorite products for the specified user code.
         */
        Page<FavoriteProduct> findByUserCode(String userCode, Pageable pageable);

        /**
         * Checks if a favorite product exists for a given user code and product ID.
         *
         * @param userCode  The user code.
         * @param productId The product ID.
         * @return True if a favorite product exists, false otherwise.
         */
        boolean existsByUserCodeAndProductId(String userCode, String productId);

        /**
         * Deletes favorite products by user code and product ID.
         *
         * @param userCode  The user code.
         * @param productId The product ID.
         */
        void deleteByUserCodeAndProductId(String userCode, String productId);
    }
