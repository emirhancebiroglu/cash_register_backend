package com.bit.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
