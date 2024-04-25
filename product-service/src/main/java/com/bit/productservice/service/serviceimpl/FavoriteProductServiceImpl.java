package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.entity.FavoriteProduct;
import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.FavoriteProductService;
import com.bit.productservice.utils.JwtUtil;
import com.bit.productservice.utils.SortApplier;
import com.bit.productservice.validators.FavoriteProductValidator;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final ProductRepository productRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final FavoriteProductValidator favoriteProductValidator;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;
    private final SortApplier sortApplier;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteProductServiceImpl.class);

    @Override
    public void addProductToFavorites(String productId) {
        logger.info("Adding product with ID {} to favorites for user {}", productId, getUserId(request));

        Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product with ID {} not found"));

        favoriteProductValidator.isProductExist(productRepository, productId);
        favoriteProductValidator.isProductFavorite(productId, getUserId(request), favoriteProductRepository);

        favoriteProductRepository.save(new FavoriteProduct(getUserId(request), product));

        logger.info("Favorite product saved successfully for user {}", getUserId(request));
    }

    @Override
    public void removeProductFromFavorites(String productId) {
        logger.info("Removing product with ID {} from favorites for user {}", productId, getUserId(request));

        favoriteProductValidator.isProductExist(productRepository, productId);
        favoriteProductValidator.isProductNotFavorite(productId, getUserId(request), favoriteProductRepository);

        favoriteProductRepository.deleteByUserIdAndProductId(getUserId(request), productId);

        logger.info("Product removed with ID {} from favorites for user {}", productId, getUserId(request));
    }

    @Override
    public List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize, String searchTerm, String stockStatus, String sortBy, String sortOrder) {
        Pageable pageable = sortApplier.applySortForFavoriteProducts(pageNo, pageSize, sortBy, sortOrder);

        logger.info("Retrieving favorite products for user {}", getUserId(request));

        Specification<FavoriteProduct> specification = Specification.where((root, query, criteriaBuilder) -> {
            Join<FavoriteProduct, Product> productJoin = root.join("product", JoinType.INNER);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("userId"), getUserId(request)));
            if (searchTerm != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("name")), "%" + searchTerm.toLowerCase() + "%"));
            }
            if (stockStatus != null) {
                predicates.add(criteriaBuilder.equal(productJoin.get("inStock"), stockStatus.equalsIgnoreCase("inStock")));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        Page<FavoriteProduct> favoriteProductsPage = favoriteProductRepository.findAll(specification, pageable);

        List<Product> favoriteProducts = favoriteProductsPage.getContent().stream()
                .map(FavoriteProduct::getProduct)
                .filter(product -> !product.isDeleted())
                .toList();

        logger.info("Favorite products retrieved successfully for user {}", getUserId(request));

        return favoriteProducts.stream()
                .map(this::convertProductToDTO)
                .toList();
    }

    /**
     * Converts a Product entity to a ProductDTO.
     *
     * @param product the Product entity to be converted
     * @return a ProductDTO object
     */
    private ProductDTO convertProductToDTO(Product product) {
        String code = product.getProductCode() != null ? product.getProductCode() : product.getBarcode();

        return new ProductDTO(
                code,
                product.getName(),
                product.getImage().getImageUrl(),
                product.getPrice(),
                product.getCategory()
        );
    }

    /**
     * Retrieves the user ID from the Authorization header of the HTTP request.
     *
     * @param request the HTTP request object
     * @return the user ID if found, otherwise null
     */
    private Long getUserId(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String token;
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }

        return null;
    }
}
