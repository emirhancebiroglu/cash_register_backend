package com.bit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;


@Data
@AllArgsConstructor
public class UpdateStockRequest {
    private Map<String, Integer> productsIdWithQuantity;
    private boolean shouldDecrease;
}
