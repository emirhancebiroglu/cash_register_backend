package com.bit.productservice.dto.updateproduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class representing a request to update an existing product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductReq {
    private String barcode;
    private String productCode;
    private String name;
    private Double price;
    private String category;
    private Integer stockAmount;
}
