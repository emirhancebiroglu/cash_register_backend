package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductInfo {
    private boolean exists;
    private double price;
    private String name;
    private int stockAmount;
}
