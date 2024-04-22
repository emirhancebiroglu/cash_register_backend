package com.bit.productservice.dto.addproduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing a request to add a new product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductReq {
    private String barcode;
    private String productCode;
    private String name;
    private Double price;
    private String category;
    private Integer stockAmount;
}
