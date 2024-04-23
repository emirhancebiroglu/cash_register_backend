package bit.salesservice.repository;

import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.DiscountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing campaign related operations.
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    /**
     * Retrieves a list of campaigns containing the specified product code.
     *
     * @param productCode the product code to search for
     * @return a list of campaigns containing the specified product code
     */
    List<Campaign> findByCodesContaining(String productCode);

    /**
     * Retrieves the first campaign containing the specified product code.
     *
     * @param code the product code to search for
     * @return the first campaign containing the specified product code
     */
    Campaign findFirstByCodesContaining(String code);

    /**
     * Retrieves the campaign with the specified name.
     *
     * @param name the name of the campaign
     * @return the campaign with the specified name
     */
    Campaign findByName(String name);

    /**
     * Retrieves a list of all active campaigns.
     *
     * @return a list of all active campaigns
     */
    List<Campaign> findAllByisInactiveIsFalse();

    /**
     * Retrieves a page of campaigns with the specified discount type.
     *
     * @param discountType the discount type to filter by
     * @param pageable     the pagination information
     * @return a page of campaigns with the specified discount type
     */
    Page<Campaign> findAllByDiscountType(DiscountType discountType, Pageable pageable);

    /**
     * Retrieves a page of campaigns containing the specified search term in their names.
     *
     * @param pageable      the pagination information
     * @param searchingTerm the search term to filter by
     * @return a page of campaigns containing the specified search term in their names
     */
    Page<Campaign> findByNameContaining(Pageable pageable, String searchingTerm);

    /**
     * Retrieves a page of active campaigns.
     *
     * @param b   indicates whether the campaigns are active or inactive
     * @param pageable the pagination information
     * @return a page of active campaigns
     */
    Page<Campaign> findAllByisInactive(boolean b, Pageable pageable);

    /**
     * Retrieves a page of campaigns containing the specified search term in their names,
     * considering whether they are active or inactive.
     *
     * @param active        indicates whether the campaigns are active or inactive
     * @param searchingTerm the search term to filter by
     * @param pageable      the pagination information
     * @return a page of campaigns containing the specified search term in their names,
     * considering whether they are active or inactive
     */
    Page<Campaign> findAllByisInactiveAndNameContaining(boolean active, String searchingTerm, Pageable pageable);

    /**
     * Retrieves a page of campaigns with the specified discount type and containing the specified search term in their names.
     *
     * @param parsedDiscountType the discount type to filter by
     * @param searchingTerm      the search term to filter by
     * @param pageable           the pagination information
     * @return a page of campaigns with the specified discount type and containing the specified search term in their names
     */
    Page<Campaign> findAllByDiscountTypeAndNameContaining(DiscountType parsedDiscountType, String searchingTerm, Pageable pageable);

    /**
     * Retrieves a page of campaigns with the specified discount type, considering whether they are active or inactive,
     * and containing the specified search term in their names.
     *
     * @param parsedDiscountType the discount type to filter by
     * @param active             indicates whether the campaigns are active or inactive
     * @param searchingTerm      the search term to filter by
     * @param pageable           the pagination information
     * @return a page of campaigns with the specified discount type, considering whether they are active or inactive,
     * and containing the specified search term in their names
     */
    Page<Campaign> findAllByDiscountTypeAndIsInactiveAndNameContaining(DiscountType parsedDiscountType, boolean active, String searchingTerm, Pageable pageable);

    /**
     * Retrieves a page of campaigns with the specified discount type and active status.
     *
     * @param parsedDiscountType the discount type to filter by
     * @param active             indicates whether the campaigns are active or inactive
     * @param pageable           the pagination information
     * @return a page of campaigns with the specified discount type and active status
     */
    Page<Campaign> findAllByDiscountTypeAndIsInactive(DiscountType parsedDiscountType, boolean active, Pageable pageable);
}
