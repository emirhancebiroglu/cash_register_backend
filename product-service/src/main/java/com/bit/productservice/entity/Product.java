package com.bit.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "products", name = "_products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "id")
    private Long id;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "stockAmount", nullable = false)
    private int stockAmount;

    @Column(name = "inStock", nullable = false)
    private boolean inStock;

    @Column(name = "category", nullable = false)
    @NonNull
    private String category;

    @Column(name = "creationDate", nullable = false)
    @NonNull
    private LocalDate creationDate = LocalDate.now();

    @Column(name = "lastUpdateDate")
    private Date lastUpdateDate;
}
