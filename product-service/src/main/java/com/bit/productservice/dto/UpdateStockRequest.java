package com.bit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * DTO class representing a request to update the stock of multiple products.
 */
@Data
@AllArgsConstructor
public class UpdateStockRequest {
    private Map<String, Integer> productsIdWithQuantity;
    private boolean shouldDecrease;
}
