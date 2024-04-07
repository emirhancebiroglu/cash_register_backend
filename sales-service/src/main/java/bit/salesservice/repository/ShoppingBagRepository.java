package bit.salesservice.repository;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingBagRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new bit.salesservice.dto.ProductReq(p.code, p.name, p.quantity, p.price) " +
            "FROM Product p WHERE p.checkout = :checkout AND p.isRemoved = false")
    List<AddAndListProductReq> findProductReqByCheckoutAndRemoved(Checkout checkout);
    Optional<Product> findByIdAndCheckoutId(Long id, Long id1);
    Product findByCodeAndCheckout(String code, Checkout currentCheckout);
    List<Product> findByCheckoutId(Long id);
}
