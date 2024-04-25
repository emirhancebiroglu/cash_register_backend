package com.bit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SpecifyStockNumberReq is a data transfer object (DTO) that represents a request to specify a stock number.
 * It contains a single integer field, {@code stockNumber}, which represents the new stock amount
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecifyStockNumberReq {
    /**
     * Field for specifying a stock number
     */
    private int stockNumber;
}
