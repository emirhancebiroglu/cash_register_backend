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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(FavoriteProductServiceImpl.class);

    @Override
    public void addProductToFavorites(String productId) {
        logger.trace("Adding product with ID {} to favorites for user {}", productId, getUserId(request));

        // Retrieve the product from the repository
        Product product = productRepository.findById(productId)
                        .orElseThrow(() -> {
                            logger.error("Failed to find product with ID {}", productId);
                            return new ProductNotFoundException("Product with ID : " + productId + " not found");
                        });

        // Validate if the product exists
        favoriteProductValidator.isProductExist(productRepository, productId);

        // Validate if the product is already a favorite for the user
        favoriteProductValidator.isProductFavorite(productId, getUserId(request), favoriteProductRepository);

        // Save the product as a favorite for the user
        favoriteProductRepository.save(new FavoriteProduct(getUserId(request), product));

        logger.trace("Favorite product saved successfully for user {}", getUserId(request));
    }

    @Override
    public void removeProductFromFavorites(String productId) {
        logger.trace("Removing product with ID {} from favorites for user {}", productId, getUserId(request));

        // Validate if the product exists
        favoriteProductValidator.isProductExist(productRepository, productId);

        // Validate if the product is a favorite for the user
        favoriteProductValidator.isProductNotFavorite(productId, getUserId(request), favoriteProductRepository);

        // Remove the product from favorites
        favoriteProductRepository.deleteByUserIdAndProductId(getUserId(request), productId);

        logger.trace("Product removed with ID {} from favorites for user {}", productId, getUserId(request));
    }

    @Override
    public List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize, String searchTerm, String stockStatus, String sortBy, String sortOrder) {
        // Apply pagination and sorting
        Pageable pageable = sortApplier.applySortForFavoriteProducts(pageNo, pageSize, sortBy, sortOrder);

        logger.trace("Retrieving favorite products for user {}", getUserId(request));

        // Define the specification for querying favorite products
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

        // Query favorite products
        Page<FavoriteProduct> favoriteProductsPage = favoriteProductRepository.findAll(specification, pageable);

        // Extract favorite products from the page
        List<Product> favoriteProducts = favoriteProductsPage.getContent().stream()
                .map(FavoriteProduct::getProduct)
                .filter(product -> !product.isDeleted())
                .toList();

        logger.trace("Favorite products retrieved successfully for user {}", getUserId(request));

        // Convert favorite products to DTOs
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
        // Determine the code (use product code if available, otherwise fallback to barcode)
        String code = product.getProductCode() != null ? product.getProductCode() : product.getBarcode();

        // Create and return a new ProductDTO instance
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
        // Retrieve the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            // Extract the token from the Authorization header
            String token = authHeader.substring(7);

            // Extract and return the user ID from the token using jwtUtil
            return jwtUtil.extractUserId(token);
        }

        // Return null if no valid Authorization header is found
        return null;
    }
}