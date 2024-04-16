package bit.reportingservice.repository;

import bit.reportingservice.entity.PaymentMethod;
import bit.reportingservice.entity.SaleReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleReportRepository extends JpaRepository<SaleReport, Long> {
    Page<SaleReport> findByCancelled(boolean b, Pageable pageable);
    Page<SaleReport> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);
}
