package bit.salesservice.service;

import bit.salesservice.dto.ProductReq;

import java.util.List;

public interface ShoppingBagService {
    void addProductToBag(ProductReq req);
    void removeProductFromBag(Long id, Integer quantity);
    void removeAll();
    List<ProductReq> getProductsInBagForCurrentCheckout();
    void returnProductFromBag(Long id, Integer quantityToReturn, Long checkoutId);
}
