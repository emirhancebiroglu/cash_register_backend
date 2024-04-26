package bit.reportingservice.repository;

import bit.reportingservice.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CampaignRepository interface provides methods to interact with the Campaign entity in the database.
 * It extends the Spring Data JPA repository interface, which automatically generates CRUD methods.
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    /**
     * Retrieves an optional of campaign based on the campaign name.
     * @param name campaign name information.
     * @return an optional of campaign.
     */
    Optional<Campaign> findByName(String name);
}
