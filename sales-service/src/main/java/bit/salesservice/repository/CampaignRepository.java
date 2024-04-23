package bit.salesservice.repository;

import bit.salesservice.entity.Campaign;
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
}
