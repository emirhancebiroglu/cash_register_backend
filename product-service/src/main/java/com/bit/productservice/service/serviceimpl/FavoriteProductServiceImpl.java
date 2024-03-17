package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.entity.FavoriteProduct;
import com.bit.productservice.entity.Product;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.FavoriteProductService;
import com.bit.productservice.utils.JwtUtil;
import com.bit.productservice.validators.FavoriteProductValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final ProductRepository productRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final FavoriteProductValidator favoriteProductValidator;
    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteProductServiceImpl.class);

    @Override
    public void addProductToFavorites(String productId) {
        logger.info("Adding product with ID {} to favorites for user {}", productId, getUserCode(request));

        favoriteProductValidator.isProductExist(productRepository, productId);

        favoriteProductRepository.save(new FavoriteProduct(getUserCode(request), productId));

        logger.info("Favorite product saved successfully for user {}", getUserCode(request));
    }

    @Override
    public void removeProductFromFavorites(String productId) {
        logger.info("Removing product with ID {} from favorites for user {}", productId, getUserCode(request));

        favoriteProductValidator.isProductExist(productRepository, productId);
        favoriteProductValidator.isProductFavorite(productId, getUserCode(request), favoriteProductRepository);

        favoriteProductRepository.deleteByUserCodeAndProductId(getUserCode(request), productId);

        logger.info("Product removed with ID {} from favorites for user {}", productId, getUserCode(request));
    }

    @Override
    public List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        logger.info("Retrieving favorite products for user {}", getUserCode(request));

        Page<FavoriteProduct> favoriteProductsPage = favoriteProductRepository.findByUserCode(getUserCode(request), pageable);
        List<Product> favoriteProducts = favoriteProductsPage.getContent().stream()
                .map(favoriteProduct -> productRepository.getProductById(favoriteProduct.getProductId()))
                .filter(product -> !product.isDeleted())
                .toList();

        logger.info("Favorite products retrieved successfully for user {}", getUserCode(request));

        return favoriteProducts.stream()
                .map(this::convertProductToDTO)
                .toList();
    }

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

    private String getUserCode(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        String token;
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            return jwtUtil.extractUsername(token);
        }

        throw new IllegalArgumentException("User not found");
    }
}
