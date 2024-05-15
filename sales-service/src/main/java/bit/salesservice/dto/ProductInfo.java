package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * DTO class representing product information.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductInfo {
    private boolean exists; // Indicates whether the product exists
    private double price; // Price of the product
    private String name; // Name of the product
    private int stockAmount; // Stock amount or quantity of the product
}
