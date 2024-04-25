package com.bit.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity class representing a favorite product.
 */
@Entity
@Table(name = "favorite-products", schema = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "user-id", nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product-id", referencedColumnName = "id")
    private Product product;

    /**
     * Constructor for creating a FavoriteProduct instance.
     *
     * @param userId  The id of the user who favorited the product.
     * @param product The ID of the favorited product.
     */
    public FavoriteProduct(Long userId, Product product) {
        this.userId = userId;
        this.product = product;
    }
}
