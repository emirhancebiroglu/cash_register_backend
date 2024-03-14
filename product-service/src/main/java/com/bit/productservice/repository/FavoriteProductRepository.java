    package com.bit.productservice.repository;

    import com.bit.productservice.entity.FavoriteProduct;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;


    @Repository
    public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, String> {
        Page<FavoriteProduct> findByUserCode(String userCode, Pageable pageable);

        boolean existsByUserCodeAndProductId(String userCode, String productId);

        void deleteByUserCodeAndProductId(String userCode, String productId);
    }
