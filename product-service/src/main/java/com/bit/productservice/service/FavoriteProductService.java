package com.bit.productservice.service;

import com.bit.productservice.dto.ProductDTO;

import java.util.List;

public interface FavoriteProductService {
    void addProductToFavorites(String productId);
    List<ProductDTO> listFavoriteProductsForCurrentUser(Integer pageNo, Integer pageSize);
    void removeProductFromFavorites(String productId);
}
