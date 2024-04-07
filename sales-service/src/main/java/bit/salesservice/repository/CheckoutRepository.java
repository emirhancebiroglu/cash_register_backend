package bit.salesservice.repository;

import bit.salesservice.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    Checkout findFirstByOrderByIdDesc();
}
