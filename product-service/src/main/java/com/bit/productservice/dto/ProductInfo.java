package com.bit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductInfo {
    private boolean exists;
    private String name;
    private double price;
    private int stockAmount;
}
