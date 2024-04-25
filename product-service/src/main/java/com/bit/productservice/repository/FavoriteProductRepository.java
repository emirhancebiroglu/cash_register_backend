    package com.bit.productservice.repository;

    import com.bit.productservice.entity.FavoriteProduct;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.domain.Specification;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    /**
     * Repository interface for managing favorite products.
     * Extends JpaRepository for CRUD operations on FavoriteProduct entities.
     */
    @Repository
    public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, String> {
        /**
         * Checks if a favorite product exists for a given user code and product ID.
         *
         * @param userId  The user code.
         * @param productId The product ID.
         * @return True if a favorite product exists, false otherwise.
         */
        boolean existsByUserIdAndProductId(Long userId, String productId);

        /**
         * Deletes favorite products by user code and product ID.
         *
         * @param userId  The user code.
         * @param productId The product ID.
         */
        void deleteByUserIdAndProductId(Long userId, String productId);

        /**
         * Finds all favorite products based on a given specification and pageable information.
         *
         * @param specification The specification to filter the favorite products.
         * @param pageable     The pageable information to paginate the results.
         * @return A page of favorite products that match the given specification and pageable information.
         */
        Page<FavoriteProduct> findAll(Specification<FavoriteProduct> specification, Pageable pageable);
    }
