package bit.salesservice.repository;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing shopping bag related operations.
 */
@Repository
public interface ShoppingBagRepository extends JpaRepository<Product, Long> {
    /**
     * Retrieves a list of products in the shopping bag for the specified checkout that are not removed.
     *
     * @param checkout the checkout object
     * @return a list of AddAndListProductReq objects representing products in the shopping bag
     */
    @Query("SELECT new bit.salesservice.dto.AddAndListProductReq(p.code, p.name, p.quantity, p.price) " +
            "FROM Product p WHERE p.checkout = :checkout AND p.isRemoved = false")
    List<AddAndListProductReq> findProductReqByCheckoutAndRemoved(Checkout checkout);

    /**
     * Retrieves a product from the shopping bag based on its code and checkout.
     *
     * @param code            the product code
     * @param currentCheckout the current checkout object
     * @return the product object
     */
    Product findByCodeAndCheckout(String code, Checkout currentCheckout);

    /**
     * Retrieves a list of products in the shopping bag for the specified checkout ID.
     *
     * @param id the checkout ID
     * @return a list of product objects
     */
    List<Product> findByCheckoutId(Long id);

    Optional<Product> findByCode(String code);
}
