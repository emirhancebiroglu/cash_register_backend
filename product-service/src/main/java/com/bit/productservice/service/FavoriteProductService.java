package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;

import java.util.List;

public interface FavoriteProductService {
    void addProductToFavorites(Long productId);
    void removeProductFromFavorites(Long productId);
    List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize);
}
