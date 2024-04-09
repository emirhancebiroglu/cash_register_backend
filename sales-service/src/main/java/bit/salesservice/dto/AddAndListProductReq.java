package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAndListProductReq {
    private String code;
    private String name;
    private int quantity;
    private double price;
}
