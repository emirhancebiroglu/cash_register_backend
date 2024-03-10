package com.bit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private String barcode;
    private String productCode;
    private String name;
    private String imageUrl;
    private double price;
    private String category;
}
