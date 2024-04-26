package bit.reportingservice.repository;

import bit.reportingservice.entity.SaleReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface extends {@link JpaRepository} and provides customized methods for interacting with the {@link SaleReport} entity.
 * It allows for the retrieval of {@link SaleReport} instances based on specifications and pagination parameters.
 */
@Repository
public interface SaleReportRepository extends JpaRepository<SaleReport, Long> {

    /**
     * Finds all {@link SaleReport} instances that match the given {@link Specification} and within the specified {@link Pageable} range.
     *
     * @param specification The {@link Specification} that defines the criteria for filtering the {@link SaleReport} instances.
     * @param pageable      The {@link Pageable} object that specifies the pagination parameters, such as the page number and size.
     * @return A {@link Page} object containing the filtered {@link SaleReport} instances, sorted and paginated according to the specified parameters.
     */
    Page<SaleReport> findAll(Specification<SaleReport> specification, Pageable pageable);
}
