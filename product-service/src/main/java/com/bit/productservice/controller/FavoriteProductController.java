package com.bit.productservice.controller;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.service.FavoriteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class responsible for managing favorite products.
 */
@RestController
@RequestMapping("api/products/favorites")
@RequiredArgsConstructor
public class FavoriteProductController {
    private final FavoriteProductService favoriteProductService;

    /**
     * Endpoint to add a product to favorites.
     *
     * @param productId The ID of the product to be added to favorites.
     * @return ResponseEntity indicating the success of the operation.
     */
    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addProductToFavorites(@PathVariable String productId) {
        favoriteProductService.addProductToFavorites(productId);
        return ResponseEntity.ok("Product added to favorites successfully!");
    }

    /**
     * Endpoint to remove a product from favorites.
     *
     * @param productId The ID of the product to be removed from favorites.
     * @return ResponseEntity indicating the success of the operation.
     */
    @DeleteMapping("/remove/{productId}")
    @Transactional
    public ResponseEntity<String> removeProductFromFavorites(@PathVariable String productId) {
        favoriteProductService.removeProductFromFavorites(productId);
        return ResponseEntity.ok("Product removed from favorites successfully!");
    }

    /**
     * Endpoint to list favorite products for the current user.
     *
     * @param pageNo   The page number for pagination (default: 0).
     * @param pageSize The size of each page for pagination (default: 15).
     * @return A list of ProductDTO objects representing favorite products.
     */
    @GetMapping("/list")
    public List<ProductDTO> listFavoriteProductsForCurrentUser(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "15") Integer pageSize,
                                                               @RequestParam(required = false) String searchTerm,
                                                               @RequestParam(required = false) String stockStatus,
                                                               @RequestParam(name = "sortBy", required = false, defaultValue = "name") String sortBy,
                                                               @RequestParam(name = "sortOrder", required = false, defaultValue = "ASC") String sortOrder) {
        return favoriteProductService.listFavoriteProductsForCurrentUser(pageNo, pageSize, searchTerm, stockStatus, sortBy, sortOrder);
    }
}
