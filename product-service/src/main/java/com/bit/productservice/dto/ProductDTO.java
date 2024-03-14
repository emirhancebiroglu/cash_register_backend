package com.bit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private String code;
    private String name;
    private String imageUrl;
    private double price;
    private String category;
}
