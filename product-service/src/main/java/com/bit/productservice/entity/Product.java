package com.bit.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    @Column(name = "barcode", nullable = false)
    private Long barcode;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @Column(name = "imageUrl", nullable = false)
    @NonNull
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
    private Date creationDate;

    @Column(name = "lastUpdateDate")
    private Date lastUpdateDate;
}
