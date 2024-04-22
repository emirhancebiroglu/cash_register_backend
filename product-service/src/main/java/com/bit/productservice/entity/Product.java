package com.bit.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing a product.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "products", name = "_products")
public class Product {
    @Id
    @Column(unique = true, name = "id")
    private String id;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "stockAmount", nullable = false)
    private int stockAmount;

    @Column(name = "inStock", nullable = false, columnDefinition = "boolean default false")
    private boolean inStock;

    @Column(name = "isDeleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate = LocalDate.now();

    @Column(name = "lastUpdateDate")
    private LocalDate lastUpdateDate;

    /**
     * Constructor with parameters.
     *
     * @param id           The ID of the product.
     * @param barcode      The barcode of the product.
     * @param productCode  The product code of the product.
     * @param name         The name of the product.
     * @param price        The price of the product.
     * @param image        The image of the product.
     * @param category     The category of the product.
     * @param stockAmount  The stock amount of the product.
     * @param creationDate The creation date of the product.
     * @param inStock      Whether the product is in stock or not.
     */
    public Product(String id, String barcode, String productCode, String name, Double price,
                   Image image, String category, Integer stockAmount, LocalDate creationDate, boolean inStock) {
        this.id = id;
        this.barcode = barcode;
        this.productCode = productCode;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.stockAmount = stockAmount;
        this.creationDate = creationDate;
        this.inStock = inStock;
    }
}
