package bit.salesservice.service;

import bit.salesservice.dto.AddAndListProductReq;

import java.util.List;

public interface ShoppingBagService {
    void addProductToBag(AddAndListProductReq req);
    void removeProductFromBag(Long id, Integer quantity);
    void removeAll();
    List<AddAndListProductReq> getProductsInBagForCurrentCheckout();
    void returnProductFromBag(Long id, Integer quantityToReturn, Long checkoutId);
}
