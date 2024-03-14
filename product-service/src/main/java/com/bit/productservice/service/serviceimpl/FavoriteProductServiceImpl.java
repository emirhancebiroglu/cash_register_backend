package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.entity.FavoriteProduct;
import com.bit.productservice.entity.Product;
import com.bit.productservice.repository.FavoriteProductRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.FavoriteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductServiceImpl implements FavoriteProductService {
    private final ProductRepository productRepository;
    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public void addProductToFavorites(String productId) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();

        if (favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new IllegalStateException("Product is already a favorite.");
        }

        favoriteProductRepository.save(new FavoriteProduct(userCode, productId));
    }

    @Override
    public void removeProductFromFavorites(String productId) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!favoriteProductRepository.existsByUserCodeAndProductId(userCode, productId)) {
            throw new IllegalStateException("Product is not favorite.");
        }

        favoriteProductRepository.deleteByUserCodeAndProductId(userCode, productId);
    }

    @Override
    public List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize) {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<FavoriteProduct> favoriteProductsPage = favoriteProductRepository.findByUserCode(userCode, pageable);
        List<Product> favoriteProducts = favoriteProductsPage.getContent().stream()
                .map(favoriteProduct -> productRepository.getProductById(favoriteProduct.getProductId()))
                .toList();

        return favoriteProducts.stream()
                .map(this::convertProductToDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertProductToDTO(Product product) {
        return new ProductDTO(
                product.getBarcode(),
                product.getProductCode(),
                product.getName(),
                product.getImage().getImageUrl(),
                product.getPrice(),
                product.getCategory()
        );
    }
}
