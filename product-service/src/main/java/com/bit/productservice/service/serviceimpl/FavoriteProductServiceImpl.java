package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.entity.FavoriteProduct;
import com.bit.productservice.entity.Product;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.FavoriteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final ProductRepository productRepository;
    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public void addProductToFavorites(Long productId) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productRepository.getProductById(productId);

        if (product == null) {
            throw new IllegalStateException("Product not found");
        }

        if (favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new IllegalStateException("Product is already a favorite.");
        }

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setUserCode(userCode);
        favoriteProduct.setProductId(productId);

        favoriteProductRepository.save(favoriteProduct);
    }

    @Override
    public void removeProductFromFavorites(Long productId) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productRepository.getProductById(productId);

        if (product == null) {
            throw new IllegalStateException("Product not found");
        }

        if (!favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new IllegalStateException("Product is not favorite.");
        }

        favoriteProductRepository.deleteByUserCodeAndProductId(userCode, productId);
    }

    @Override
    public List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FavoriteProduct> favoriteProducts = favoriteProductRepository.findByUserCode(userCode);
        List<Product> products = new ArrayList<>();

        for (FavoriteProduct favoriteProduct : favoriteProducts) {
            Product product = productRepository.getProductById(favoriteProduct.getProductId());
            products.add(product);
        }

        if (products.isEmpty()) {
            throw new IllegalStateException("empty");
        }

        products.sort(Comparator.comparing(Product::getName));

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), favoriteProducts.size());

        if (start > favoriteProducts.size()) {
            return Collections.emptyList();
        }

        return new PageImpl<>(products.subList(start, end), pageable, favoriteProducts.size())
                .map(this::convertProductToDTO)
                .getContent();
    }

    private ProductDTO convertProductToDTO(Product product) {
        return new ProductDTO(
                product.getBarcode(),
                product.getProductCode(),
                product.getName(),
                product.getImageUrl(),
                product.getPrice(),
                product.getCategory()
        );
    }
}
