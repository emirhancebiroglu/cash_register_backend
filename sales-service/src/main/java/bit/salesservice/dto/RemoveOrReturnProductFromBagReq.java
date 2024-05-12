package bit.salesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveOrReturnProductFromBagReq {
    private Long checkoutId;
    private String code;
    private Integer quantity;
}
