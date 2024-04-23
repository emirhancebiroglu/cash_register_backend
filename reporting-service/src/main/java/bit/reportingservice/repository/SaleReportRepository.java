package bit.reportingservice.repository;

import bit.reportingservice.entity.PaymentMethod;
import bit.reportingservice.entity.SaleReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleReportRepository extends JpaRepository<SaleReport, Long> {
    /**
     * Retrieves a page of sale reports based on the cancellation status.
     * @param b the cancellation status to filter by.
     * @param pageable pagination information.
     * @return a page of sale reports.
     */
    Page<SaleReport> findByCancelled(boolean b, Pageable pageable);

    /**
     * Retrieves a page of sale reports based on the payment method.
     * @param paymentMethod the payment method to filter by.
     * @param pageable pagination information.
     * @return a page of sale reports.
     */
    Page<SaleReport> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);
}
