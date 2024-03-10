    package com.bit.productservice.repository;

    import com.bit.productservice.entity.FavoriteProduct;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.List;

    @Repository
    public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
        List<FavoriteProduct> findByUserCode(String userCode);

        boolean existsByUserCodeAndProductId(String userCode, Long productId);

        void deleteByUserCodeAndProductId(String userCode, Long productId);
    }
