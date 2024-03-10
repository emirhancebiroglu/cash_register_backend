package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.service.FavoriteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products/favorites")
@RequiredArgsConstructor
public class FavoriteProductController {
    private final FavoriteProductService favoriteProductService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addProductToFavorites(@PathVariable Long productId) {
        favoriteProductService.addProductToFavorites(productId);
        return ResponseEntity.ok("Product added to favorites successfully!");
    }

    @DeleteMapping("/remove/{productId}")
    @Transactional
    public ResponseEntity<String> removeProductFromFavorites(@PathVariable Long productId) {
        favoriteProductService.removeProductFromFavorites(productId);
        return ResponseEntity.ok("Product removed from favorites successfully!");
    }

    @GetMapping("/list")
    public List<ProductDTO> listFavoriteProductsForCurrentUser(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "15") Integer pageSize) {
        return favoriteProductService.listFavoriteProductsForCurrentUser(pageNo, pageSize);
    }
}
