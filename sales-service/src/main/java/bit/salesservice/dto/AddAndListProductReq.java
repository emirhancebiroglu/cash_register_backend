package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddAndListProductReq {
    private String code;
    private String name;
    private int quantity;
    private double price;
}
